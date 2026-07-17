package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.ScheduleRequest;
import com.example.demo.DTOs.ResponseDTO.ScheduleResponse;
import com.example.demo.Services.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleController {

    private final ScheduleService scheduleService;

    /**
     * API Tạo khung giờ làm việc mới cho Bác sĩ (Dành cho Admin/Bác sĩ)
     * URL: POST http://localhost:8080/api/schedules
     */
    @PostMapping
    public ResponseEntity<String> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        log.info("REST request tạo lịch làm việc mới cho Doctor ID: {}", request.getDoctorId());
        scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo khung giờ làm việc mới thành công.");
    }

    /**
     * API Lấy danh sách lịch khám CÒN TRỐNG SLOTS của bác sĩ theo ngày cụ thể (Dành cho Bệnh nhân chọn đặt lịch)
     * URL: GET http://localhost:8080/api/schedules/available?doctorId=1&date=2026-06-20
     */
    @GetMapping("/available")
    public ResponseEntity<List<ScheduleResponse>> getAvailableSchedules(
            @RequestParam Integer doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("REST request lấy danh sách lịch khám trống của Doctor ID: {} vào ngày: {}", doctorId, date);
        List<ScheduleResponse> availableSchedules = scheduleService.getAvailableSchedules(doctorId, date);
        return ResponseEntity.ok(availableSchedules);
    }
    /**
     * API Giữ chỗ trước khi đặt lịch (Hoặc để test concurrency)
     * URL: POST http://localhost:8080/api/schedules/{id}/reserve
     */
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveSlot(@PathVariable Integer id) {
        scheduleService.reserveSlot(id);
        return ResponseEntity.ok().build();
    }

    /**
     * API Nhả chỗ khi hủy lịch (Thường gọi nội bộ, nhưng có thể mở endpoint nếu cần)
     * URL: POST http://localhost:8080/api/schedules/{id}/release
     */
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseSlot(@PathVariable Integer id) {
        scheduleService.releaseSlot(id);
        return ResponseEntity.ok().build();
    }

    /**
     * API: Lấy toàn bộ danh sách lịch làm việc của một bác sĩ bất kỳ (Cho Admin quản lý)
     * URL: GET http://localhost:8080/api/schedules/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByDoctorId(@PathVariable Integer doctorId) {
        log.info("REST request lấy toàn bộ lịch trình của Doctor ID: {}", doctorId);
        // Giả định tầng service của bạn đã có hàm tìm kiếm toàn bộ lịch theo doctorId
        List<ScheduleResponse> schedules = scheduleService.getAllSchedulesByDoctorId(doctorId);
        return ResponseEntity.ok(schedules);
    }

    /**
     * API: Bác sĩ tự xem toàn bộ lịch làm việc của chính mình
     * URL: GET http://localhost:8080/api/schedules/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<ScheduleResponse>> getMySchedules() {
        Integer mockCurrentUserId = 2; // Sau này thay bằng ID từ JWT Token / Principal
        log.info("REST request Bác sĩ tự lấy lịch trình của chính mình, User ID: {}", mockCurrentUserId);

        List<ScheduleResponse> mySchedules = scheduleService.getSchedulesByDoctorUserId(mockCurrentUserId);
        return ResponseEntity.ok(mySchedules);
    }
}