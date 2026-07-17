package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.PatientRequest;
import com.example.demo.DTOs.ResponseDTO.ApiResponse;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.DTOs.ResponseDTO.PatientResponse;
import com.example.demo.Services.PatientService;
import com.example.demo.Security.CustomUserDetails; // Import CustomUserDetails của bạn
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Thêm import này
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    /**
     * API: Lấy thông tin cá nhân của người dùng hiện tại
     * Frontend: patientService.getMyProfile() -> GET /patients/me
     */
    @GetMapping("/patients/me")
    public ResponseEntity<ApiResponse<PatientResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer userId = userDetails.getUserId();
        log.info("REST request lấy thông tin cá nhân của User ID: {}", userId);

        PatientResponse profile = patientService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin cá nhân thành công", profile));
    }

    /**
     * API: Cập nhật thông tin cá nhân của người dùng hiện tại
     * Frontend: patientService.updateMyProfile(data) -> PUT /patients/me
     */
    @PutMapping("/patients/me")
    public ResponseEntity<ApiResponse<PatientResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatientRequest request) {

        Integer userId = userDetails.getUserId();
        log.info("REST request cập nhật thông tin cá nhân của User ID: {}", userId);

        PatientResponse updated = patientService.updateProfileByUserId(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin cá nhân thành công", updated));
    }

    /**
     * API: Xem danh sách các lịch hẹn khám sắp diễn ra sắp tới
     * Frontend: patientService.getUpcomingAppointments() -> GET /patients/me/appointments/upcoming
     */
    @GetMapping("/patients/me/appointments/upcoming")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUpcomingAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer userId = userDetails.getUserId();
        log.info("REST request lấy lịch hẹn sắp tới của User ID: {}", userId);

        List<AppointmentResponse> list = patientService.getUpcomingAppointments(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn sắp tới thành công", list));
    }

    /**
     * API: Xem lịch sử toàn bộ các ca đã khám hoặc bị hủy
     * Frontend: patientService.getAppointmentHistory() -> GET /patients/me/appointments/history
     */
    @GetMapping("/patients/me/appointments/history")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer userId = userDetails.getUserId();
        log.info("REST request lấy lịch sử khám bệnh của User ID: {}", userId);

        List<AppointmentResponse> list = patientService.getAppointmentHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch sử khám bệnh thành công", list));
    }

    /**
     * ADMIN: Lấy toàn bộ danh sách bệnh nhân trong hệ thống
     * Frontend: patientService.getAll() -> GET /admin/patients
     */
    @GetMapping("/admin/patients")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {
        log.info("REST request từ Admin lấy toàn bộ danh sách bệnh nhân");

        List<PatientResponse> list = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bệnh nhân thành công", list));
    }

    /**
     * ADMIN/USER: Lấy chi tiết thông tin một bệnh nhân theo ID cụ thể
     * Frontend: patientService.getById(id) -> GET /patients/{id}
     */
    @GetMapping("/patients/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable Integer id) {
        log.info("REST request lấy chi tiết bệnh nhân ID: {}", id);

        PatientResponse patient = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết thông tin bệnh nhân thành công", patient));
    }
}