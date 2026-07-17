package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.CreateDoctorRequest;
import com.example.demo.DTOs.RequestDTO.UpdateDoctorRequest;
import com.example.demo.DTOs.ResponseDTO.DoctorResponse;
import com.example.demo.Entities.Doctor;
import com.example.demo.Services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    // Giả định bạn đã tiêm hoặc gọi AppointmentService để lấy lịch hẹn của bác sĩ
    // private final AppointmentService appointmentService;

    /**
     * Sửa: Trả về ResponseEntity<List<DoctorResponse>> thay vì Entity Doctor để tránh lỗi Lazy Loading
     * Khớp: api.get('/doctors', { params })
     */
    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Integer minYears) {
        return ResponseEntity.ok(doctorService.getDoctors(specialty, minYears));
    }

    /**
     * Sửa: Trả về ResponseEntity<DoctorResponse>
     * Khớp: api.get(`/doctors/${id}`)
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Integer id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    /**
     * Sửa: Trả về ResponseEntity<DoctorResponse>
     * Khớp: api.get('/doctors/me')
     */
    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile() {
        // Tạm thời truyền cứng User ID = 2, sau này bạn thay bằng ID từ JWT Token / Principal
        Integer mockCurrentUserId = 2;
        return ResponseEntity.ok(doctorService.getDoctorByUserId(mockCurrentUserId));
    }

    /**
     * Sửa: Trả về ResponseEntity<DoctorResponse> sau khi cập nhật thông tin cá nhân
     * Khớp: api.put('/doctors/me', data)
     */
    @PutMapping("/me")
    public ResponseEntity<DoctorResponse> updateMyProfile(@RequestBody Doctor updatedDoctor) {
        Integer mockCurrentUserId = 2; // Thay bằng ID từ JWT Token sau
        return ResponseEntity.ok(doctorService.updateDoctorProfile(mockCurrentUserId, updatedDoctor));
    }

    /**
     * 🛠️ ĐÃ ĐỒNG BỘ: Bác sĩ xem danh sách lịch hẹn của chính mình (có lọc theo ngày)
     * Khớp: api.get('/doctors/me/appointments', { params })
     */
    @GetMapping("/me/appointments")
    public ResponseEntity<?> getMyAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Integer mockCurrentUserId = 2; // Thay bằng ID từ JWT Token sau

        // Bạn gọi sang hàm xử lý lấy lịch hẹn của Bác sĩ tương ứng
        // Ví dụ: return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorAndDate(mockCurrentUserId, date));
        return ResponseEntity.ok().build();
    }

    /**
     * 🛠️ ĐÃ ĐỒNG BỘ: Endpoint dành cho Admin tạo bác sĩ
     * Sửa: Đồng bộ kiểu trả về trong Service là DoctorResponse thay vì Doctor
     * Khớp: api.post('/doctors', data)
     */
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        DoctorResponse newDoctor = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDoctor);
    }

    /**
     * 🛠️ ĐÃ ĐỒNG BỘ: Endpoint dành cho Admin cập nhật thông tin bác sĩ theo ID
     * Sửa: Trả về ResponseEntity<DoctorResponse>
     * Khớp: api.put(`/doctors/${id}`, data)
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctorByAdmin(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDoctorRequest request) {
        DoctorResponse updated = doctorService.updateDoctorByAdmin(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 🛠️ ĐÃ ĐỒNG BỘ: Endpoint dành cho Admin xóa/vô hiệu hóa bác sĩ
     * Khớp: api.delete(`/doctors/${id}`)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Integer id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build(); // Trả về HTTP Code 204 No Content thông báo xóa thành công
    }


    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Bạn cần viết thêm method getAvailableSlots trong DoctorService (hoặc AppointmentService) của bạn.
        // Dưới đây là ví dụ gọi qua doctorService:
        List<String> availableSlots = doctorService.getAvailableSlots(id, date);

        return ResponseEntity.ok(availableSlots);
    }
}