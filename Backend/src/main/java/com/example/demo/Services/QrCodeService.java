package com.example.demo.Services;

public interface QrCodeService {

    /**
     * Mã hóa thông tin lịch hẹn thành chuỗi hình ảnh QR Code dạng Base64
     * Giúp Frontend (Web/Mobile) hiển thị trực tiếp lên màn hình mà không cần lưu file ở ổ cứng server
     *
     * @param appointmentId ID của lịch hẹn cần tạo mã check-in
     * @return Chuỗi String Base64 đại diện cho ảnh PNG
     * @throws Exception Lỗi trong quá trình mã hóa ma trận dữ liệu của ZXing
     */
    String generateAppointmentQrCode(Integer appointmentId) throws Exception;
}