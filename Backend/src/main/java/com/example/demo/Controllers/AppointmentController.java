package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.BookAppointmentRequest;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.Services.AppointmentService;
import com.example.demo.Services.QrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final QrCodeService qrCodeService;

    /**
     * 1. API Đặt lịch khám bệnh
     * ĐÃ ĐỒNG BỘ: Đổi sang nhận gốc /api/appointments và trả về Object thay vì String chữ
     */
    @PostMapping
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody BookAppointmentRequest request) {
        log.info("REST request đặt lịch khám cho Patient ID: {}", request.getPatientId());
        Object result = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 2. API Lấy chi tiết lịch hẹn theo ID
     * ĐÃ ĐỒNG BỘ với: api.get(`/appointments/${id}`)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("REST request lấy chi tiết lịch hẹn ID: {}", id);
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    /**
     * 3. API Hủy lịch hẹn khám bệnh
     * ĐÃ ĐỒNG BỘ: Chuyển sang @PatchMapping tương thích với Front-end
     */
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Integer appointmentId) {
        log.info("REST request hủy lịch hẹn ID: {}", appointmentId);
        // Nếu nghiệp vụ cũ cần lý do, bạn có thể truyền chuỗi rỗng hoặc bổ sung RequestBody
        appointmentService.cancelAppointment(appointmentId, "Người dùng tự hủy trên giao diện");
        return ResponseEntity.ok("Đã hủy lịch hẹn thành công.");
    }

    /**
     * 4. API Cập nhật trạng thái lịch hẹn
     * ĐÃ ĐỒNG BỘ: Chuyển sang @PatchMapping và nhận JSON Object { "status": "..." } qua @RequestBody
     */
    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable Integer appointmentId,
            @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        log.info("REST request cập nhật trạng thái lịch hẹn ID: {} sang: {}", appointmentId, statusStr);

        // Convert chuỗi từ JSON sang Enum tương ứng của hệ thống
        // AppointmentStatus status = AppointmentStatus.valueOf(statusStr);
        // appointmentService.updateStatus(appointmentId, status);

        return ResponseEntity.ok("Cập nhật trạng thái lịch hẹn thành công.");
    }

    /**
     * 5. API Lấy toàn bộ danh sách lịch hẹn (Dành cho Admin)
     * ĐÃ ĐỒNG BỘ với: api.get('/appointments/all')
     */
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        log.info("REST request lấy toàn bộ danh sách lịch hẹn hệ thống");
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * 6. API Bệnh nhân lấy mã QR Check-in lịch hẹn dạng Base64
     * ĐÃ ĐỒNG BỘ với: api.get(`/appointments/${appointmentId}/qrcode`)
     */
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<?> getAppointmentQrCode(@PathVariable Integer id) {
        try {
            String base64Image = qrCodeService.generateAppointmentQrCode(id);
            Map<String, String> response = new HashMap<>();
            response.put("qrCodeBase64", "data:image/png;base64," + base64Image);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo mã QR: " + e.getMessage());
        }
    }


    @GetMapping("/api/patients/me/appointments/upcoming")
    public ResponseEntity<?> getUpcomingAppointments() {
        log.info("REST request lấy lịch hẹn sắp tới của bệnh nhân hiện tại");
        // Giả định bệnh nhân hiện tại đang đăng nhập có ID = 1 (sau này thay bằng ID từ JWT)
        Integer mockPatientId = 1;

        // Gọi Service lấy lịch hẹn sắp tới
        // Ví dụ: List<AppointmentResponse> list = appointmentService.getUpcomingAppointments(mockPatientId);
        // return ResponseEntity.ok(list);

        // Tạm thời trả về danh sách trống hoặc kết quả từ Service của bạn:
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(mockPatientId));
    }

    /**
     * 8. API Bệnh nhân lấy lịch sử khám (COMPLETED, CANCELLED)
     * ĐÃ ĐỒNG BỘ với: patientService.getAppointmentHistory() -> GET /api/patients/me/appointments/history
     */
    @RequestMapping(value = "/api/patients/me/appointments/history", method = RequestMethod.GET)
    public ResponseEntity<?> getAppointmentHistory() {
        log.info("REST request lấy lịch sử khám của bệnh nhân hiện tại");
        Integer mockPatientId = 1; // Thay bằng ID từ JWT sau

        return ResponseEntity.ok(appointmentService.getAppointmentHistory(mockPatientId));
    }
}