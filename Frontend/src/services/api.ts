import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'
import toast from 'react-hot-toast'

// Định nghĩa lại cấu trúc mở rộng cho AxiosRequestConfig để không bị lỗi TypeScript cảnh báo trường _retry
interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
})

// Các biến phục vụ việc xếp hàng hàng đợi khi refresh token (Chống Race Condition)
let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

// Hàm push các request bị hoãn vào hàng đợi
const subscribeTokenRefresh = (cb: (token: string) => void) => {
  refreshSubscribers.push(cb)
}

// Hàm chạy lại tất cả các request đang xếp hàng sau khi đã đổi token thành công
const onRefreshed = (token: string) => {
  refreshSubscribers.map((cb) => cb(token))
  refreshSubscribers = []
}

// ─── Request interceptor: đính kèm Access Token ───────────────────────────────
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// ─── Response interceptor: xử lý lỗi & refresh token tập trung ───────────────
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<any>) => {
    const originalRequest = error.config as CustomAxiosRequestConfig

    // 1. Nếu lỗi 401 (Hết hạn Token) và request này chưa từng thử thử lại (_retry)
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      
      if (isRefreshing) {
        // Nếu đang có một request khác đi đổi token rồi, request này chỉ cần đứng đợi trong hàng đợi
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            resolve(api(originalRequest))
          })
        })
      }

      // Đánh dấu request này đang thực hiện đổi token, chặn các request sau nhảy vào bổ sung
      originalRequest._retry = true
      isRefreshing = true

      const refreshToken = localStorage.getItem('refreshToken')

      if (refreshToken) {
        try {
          // Gọi API refresh token riêng lẻ sử dụng thực thể axios gốc (tránh lặp vô hạn interceptor)
          const response = await axios.post('/api/auth/refresh', { refreshToken })
          
          // Khắc phục an toàn: Kiểm tra cấu trúc data trả về từ backend của bạn
          const newToken = response.data?.data?.accessToken || response.data?.accessToken
          
          if (newToken) {
            localStorage.setItem('accessToken', newToken)
            
            // Kích hoạt giải phóng toàn bộ các request đang đứng xếp hàng chờ đợi
            onRefreshed(newToken)
            isRefreshing = false

            // Thực thi lại chính request lỗi ban đầu với token mới
            originalRequest.headers.Authorization = `Bearer ${newToken}`
            return api(originalRequest)
          }
        } catch (refreshError) {
          // Đổi token thất bại (RefreshToken cũng hết hạn) -> Đăng xuất toàn bộ
          isRefreshing = false
          refreshSubscribers = []
          localStorage.clear()
          window.location.href = '/login'
          return Promise.reject(refreshError)
        }
      } else {
        localStorage.clear()
        window.location.href = '/login'
      }
    }

    // 2. Hiển thị thông báo lỗi bằng Toast ẩn cho người dùng trực quan
    // Ưu tiên lấy message chi tiết từ ApiResponse của Backend định nghĩa, nếu không có lấy chuỗi mặc định
    const message = error.response?.data?.message || 'Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.'
    
    // Chỉ bật Toast thông báo lỗi nếu đó không phải lỗi hết hạn phiên đăng nhập 401
    if (error.response?.status !== 401) {
      toast.error(message)
    }

    return Promise.reject(error)
  }
)

export default api