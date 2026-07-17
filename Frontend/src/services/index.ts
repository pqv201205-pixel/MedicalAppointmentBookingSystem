
import api from './api'
import type {
  ApiResponse, AuthResponse, LoginRequest, RegisterRequest,
  Patient, Doctor, DoctorSchedule, Appointment, Specialty,
  Notification, DashboardSummary,ScheduleResponse,MedicalRecordRequest, MedicalRecord,
  ReviewRequest,Review,WaitingRequest
} from '@/types'

// ─── Auth ─────────────────────────────────────────────────────────────────────
export const authService = {
  login: (data: LoginRequest) =>
    api.post<ApiResponse<AuthResponse>>('/auth/login', data),

  register: (data: RegisterRequest) =>
    api.post<ApiResponse<{ userId: number; username: string }>>('/auth/register', data),

  logout: () => api.post('/auth/logout'),

  refreshToken: (refreshToken: string) =>
    api.post<ApiResponse<AuthResponse>>('/auth/refresh', { refreshToken }),

  forgotPassword: (email: string) =>
    api.post('/auth/forgot-password', { email }),

  resetPassword: (data: { email: string; otp: string; newPassword: string }) =>
    api.post('/auth/reset-password', data),

  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    api.post('/auth/change-password', data),
}

// ─── Email & OTP (MỚI BỔ SUNG) ────────────────────────────────────────────────
export const emailService = {
  // Gửi mã OTP kích hoạt hoặc quên mật khẩu về email
  sendOtp: (email: string) =>
    api.post<{ message: string }>('/email/send-otp', { email }),
}

// ─── Patient ──────────────────────────────────────────────────────────────────
export const patientService = {
  getMyProfile: () =>
    api.get<ApiResponse<Patient>>('/patients/me'),

  updateMyProfile: (data: Partial<Patient>) =>
    api.put<ApiResponse<Patient>>('/patients/me', data),

  getUpcomingAppointments: () =>
    api.get<ApiResponse<Appointment[]>>('/patients/me/appointments/upcoming'),

  getAppointmentHistory: () =>
    api.get<ApiResponse<Appointment[]>>('/patients/me/appointments/history'),

  // ADMIN
  getAll: () =>
    api.get<ApiResponse<Patient[]>>('/admin/patients'),

  getById: (id: number) =>
    api.get<ApiResponse<Patient>>(`/patients/${id}`),
}

// ─── Doctor & Doctor CRUD (ĐÃ CẬP NHẬT CHUẨN ADMIN) ───────────────────────────
export const doctorService = {
  getDoctors: (params?: { specialty?: string; minYears?: number }) =>
    api.get<ApiResponse<Doctor[]>>('/doctors', { params }),

  getDoctorById: (id: number) =>
    api.get<ApiResponse<Doctor>>(`/doctors/${id}`),

  getMyProfile: () =>
    api.get<ApiResponse<Doctor>>('/doctors/me'),

  updateMyProfile: (data: Partial<Doctor>) =>
    api.put<ApiResponse<Doctor>>('/doctors/me', data),

  getMyAppointments: (date?: string) =>
  api.get<ApiResponse<Appointment[]>>('/doctors/me/appointments', { params: date ? { date } : {} }),

  // 🛠️ CÁC CHỨC NĂNG CRUD DOCTOR DÀNH CHO ADMIN (MỚI BỔ SUNG)
  createDoctor: (data: any) =>
    api.post<ApiResponse<Doctor>>('/doctors', data),

  updateDoctorByAdmin: (id: number, data: any) =>
    api.put<ApiResponse<Doctor>>(`/doctors/${id}`, data),

  deleteDoctor: (id: number) =>
    api.delete<ApiResponse<Void>>(`/doctors/${id}`),
}

// ─── Schedules (ĐÃ ĐỒNG BỘ 100% VỚI BACKEND MỚI) ──────────────────────
export const scheduleService = {
  // Đồng bộ với @GetMapping("/available")
  getAvailableSlots: (doctorId: number, date: string) =>
    api.get<ApiResponse<ScheduleResponse[]>>('/schedules/available', { params: { doctorId, date } }),

  // ĐÃ SỬA: Đổi kiểu trả về thành string để khớp với ResponseEntity<String> từ Backend
  createSchedule: (data: Omit<DoctorSchedule, 'scheduleId' | 'doctorId' | 'doctorName' | 'dayOfWeekLabel'>) =>
    api.post<ApiResponse<string>>('/schedules', data),

  // Đồng bộ với @PostMapping("/{id}/reserve")
  reserveSlot: (scheduleId: number) =>
    api.post(`/schedules/${scheduleId}/reserve`),

  // Đồng bộ với @PostMapping("/{id}/release")
  releaseSlot: (scheduleId: number) =>
    api.post(`/schedules/${scheduleId}/release`),
    
  // ĐÃ SỬA: Trỏ về đúng Controller quản lý lịch làm việc (/schedules) thay vì /doctors
  getSchedules: (doctorId: number) =>
    api.get<ApiResponse<ScheduleResponse[]>>(`/schedules/doctor/${doctorId}`),

  // ĐÃ SỬA: Trỏ về đúng Controller quản lý lịch làm việc (/schedules/me)
  getMySchedules: () =>
    api.get<ApiResponse<ScheduleResponse[]>>('/schedules/me'),
}

// ─── Appointment & QR Code (ĐÃ BỔ SUNG QR CODE) ────────────────────────────────
export const appointmentService = {
  book: (data: {
    doctorId: number
    appointmentDate: string
    timeSlot: string
    symptoms?: string
    notes?: string
  }) => api.post<ApiResponse<Appointment>>('/appointments', data),

  getById: (id: number) =>
    api.get<ApiResponse<Appointment>>(`/appointments/${id}`),

  cancel: (id: number) =>
    api.patch<ApiResponse<Appointment>>(`/appointments/${id}/cancel`),

  updateStatus: (id: number, status: string) =>
    api.patch<ApiResponse<Appointment>>(`/appointments/${id}/status`, { status }),

  getAll: () =>
    api.get<ApiResponse<Appointment[]>>('/appointments/all'),

  // 🏥 LẤY MÃ QR CHECK-IN LỊCH HẸN (MỚI BỔ SUNG)
  // Khớp với @GetMapping("/{id}/qrcode") tại AppointmentController
  getQrCode: (appointmentId: number) =>
    api.get<{ qrCodeBase64: string }>(`/appointments/${appointmentId}/qrcode`),
}

// ─── Specialty (ĐỒNG BỘ 100% VỚI SPECIALIZATIONCONTROLLER) ────────────────────
export const specialtyService = {
  // Thay đổi URL từ '/specialties' thành '/specializations' để khớp chính xác với @RequestMapping Backend
  getAll: () =>
    api.get<ApiResponse<Specialty[]>>('/specializations'),

  getById: (id: number) =>
    api.get<ApiResponse<Specialty>>(`/specializations/${id}`),

  create: (data: Omit<Specialty, 'specialtyId'>) =>
    api.post<ApiResponse<Specialty>>('/specializations', data),

  update: (id: number, data: Partial<Specialty>) =>
    api.put<ApiResponse<Specialty>>(`/specializations/${id}`, data),

  delete: (id: number) =>
    api.delete(`/specializations/${id}`),
}

// ─── Notification (ĐỒNG BỘ VỚI NOTIFICATIONCONTROLLER) ───────────────────────
export const notificationService = {
  // Khớp @RequestMapping("/api/notifications")
  getAll: () =>
    api.get<ApiResponse<Notification[]>>('/notifications'),

  getUnreadCount: () =>
    api.get<ApiResponse<number>>('/notifications/unread-count'),

  markAsRead: (id: number) =>
    api.patch<ApiResponse<Void>>(`/notifications/${id}/read`),
}

// ─── Dashboard (ĐỒNG BỘ VỚI DASHBOARDCONTROLLER) ──────────────────────────────
export const dashboardService = {
  // Khớp với @GetMapping("/summary") trong DashboardController
  getSummary: () =>
    api.get<ApiResponse<DashboardSummary>>('/dashboard/summary'),
}

// ─── Medical Record Service (ĐỒNG BỘ 100% VỚI MEDICALRECORDCONTROLLER) ───
export const medicalRecordService = {
  
  /**
   * Bác sĩ tạo mới hồ sơ bệnh án cho bệnh nhân sau khi khám xong
   * URL: POST /medical-records
   * Lưu ý: Do Backend trả về ResponseEntity<String>, không bọc ApiResponse nên nhận trực tiếp string.
   */
  create: (data: MedicalRecordRequest) =>
    api.post<string>('/medical-records', data),

  /**
   * Tải lên tài liệu, hình ảnh chụp chiếu hoặc đơn thuốc đính kèm bệnh án
   * URL: POST /medical-records/{recordId}/upload
   * Ghi chú: Sử dụng FormData để truyền mảng File (MultipartFile[]) qua API
   */
  uploadDocuments: (recordId: number, files: File[]) => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('files', file); // Khớp với @RequestParam("files") ở Backend
    });

    return api.post<string>(`/medical-records/${recordId}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  /**
   * Lấy toàn bộ lịch sử bệnh án từ trước đến nay của một Bệnh nhân cụ thể
   * URL: GET /medical-records/patient/{patientId}
   * Lưu ý: Backend đang trả về ResponseEntity<List<MedicalRecord>> (mảng raw không bọc ApiResponse)
   */
  getPatientHistory: (patientId: number) =>
    api.get<MedicalRecord[]>(`/medical-records/patient/${patientId}`),
}
// ─── Review Service (ĐỒNG BỘ 100% VỚI REVIEWCONTROLLER) ───────────────────────
export const reviewService = {
  
  /**
   * Bệnh nhân gửi đánh giá, phản hồi và chấm điểm sao sau khi hoàn thành lịch hẹn
   * URL: POST /reviews
   * Lưu ý: Backend trả về ResponseEntity<String> thô (không bọc ApiResponse)
   */
  createReview: (data: ReviewRequest) =>
    api.post<string>('/reviews', data),

  /**
   * Lấy danh sách toàn bộ đánh giá của một Bác sĩ cụ thể để hiển thị lên UI trang cá nhân
   * URL: GET /reviews/doctor/{doctorId}
   * Lưu ý: Backend trả về ResponseEntity<List<Review>> thô (không bọc ApiResponse)
   */
  getByDoctorId: (doctorId: number) =>
    api.get<Review[]>(`/reviews/doctor/${doctorId}`),
}
// ─── Waiting List Service (ĐỒNG BỘ 100% VỚI WAITINGLISTCONTROLLER) ───────────
export const waitingListService = {

  /**
   * Bệnh nhân đăng ký xếp hàng vào danh sách chờ khi một khung giờ khám đã bị đầy chỗ
   * URL: POST /waiting-list/add
   * Lưu ý: Backend trả về ResponseEntity<String> thô (không bọc ApiResponse)
   */
  addToWaitingList: (data: WaitingRequest) =>
    api.post<string>('/waiting-list/add', data),

  /**
   * ADMIN: Chủ động kích hoạt quét duyệt thủ công danh sách chờ cho một lịch làm việc cụ thể
   * URL: POST /waiting-list/process/{scheduleId}
   * Lưu ý: Backend trả về ResponseEntity<String> thô (không bọc ApiResponse)
   */
  triggerProcessWaitingList: (scheduleId: number) =>
    api.post<string>(`/waiting-list/process/${scheduleId}`),
}