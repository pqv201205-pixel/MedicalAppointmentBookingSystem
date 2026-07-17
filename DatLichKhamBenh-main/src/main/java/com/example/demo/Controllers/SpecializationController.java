package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.SpecializationRequest;
import com.example.demo.DTOs.ResponseDTO.ApiResponse;
import com.example.demo.DTOs.ResponseDTO.SpecializationResponse;
import com.example.demo.Services.SpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
@Slf4j
public class SpecializationController {

    private final SpecializationService specializationService;

    /**
     * API: Lấy toàn bộ danh sách chuyên khoa (Có tích hợp Cache ngoài trang chủ)
     * URL: GET http://localhost:8080/api/specializations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecializationResponse>>> getAllSpecializations() {
        log.info("REST request lấy danh sách toàn bộ chuyên khoa");
        List<SpecializationResponse> list = specializationService.getAllSpecializations();

        // ĐÃ SỬA: Dùng ApiResponse.success static method
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chuyên khoa thành công", list));
    }

    /**
     * API: Lấy chi tiết một chuyên khoa theo ID
     * URL: GET http://localhost:8080/api/specializations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecializationResponse>> getSpecializationById(@PathVariable Integer id) {
        log.info("REST request lấy chi tiết chuyên khoa ID: {}", id);
        SpecializationResponse response = specializationService.getSpecializationById(id);

        // ĐÃ SỬA: Dùng ApiResponse.success static method
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết chuyên khoa thành công", response));
    }

    /**
     * ADMIN: Tạo mới một chuyên khoa bệnh viện
     * URL: POST http://localhost:8080/api/specializations
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SpecializationResponse>> createSpecialization(
            @Valid @RequestBody SpecializationRequest request) {
        log.info("REST request từ Admin tạo chuyên khoa mới: {}", request.getName());
        SpecializationResponse created = specializationService.createSpecialization(request);

        // ĐÃ SỬA: Bọc ApiResponse cho hàm POST tạo mới
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo chuyên khoa mới thành công", created));
    }

    /**
     * ADMIN: Chỉnh sửa tên hoặc mô tả chuyên khoa
     * URL: PUT http://localhost:8080/api/specializations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecializationResponse>> updateSpecialization(
            @PathVariable Integer id,
            @Valid @RequestBody SpecializationRequest request) {
        log.info("REST request từ Admin cập nhật chuyên khoa ID: {}", id);
        SpecializationResponse updated = specializationService.updateSpecialization(id, request);

        // ĐÃ SỬA: Bọc ApiResponse cho hàm PUT cập nhật thông tin
        return ResponseEntity.ok(ApiResponse.success("Cập nhật chuyên khoa thành công", updated));
    }

    /**
     * ADMIN: Xóa một chuyên khoa (Service sẽ tự chặn nếu chuyên khoa đang có bác sĩ trực)
     * URL: DELETE http://localhost:8080/api/specializations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialization(@PathVariable Integer id) {
        log.warn("REST request từ Admin xóa chuyên khoa ID: {}", id);
        specializationService.deleteSpecialization(id);

        // ĐÃ SỬA: Thay vì trả về 204 trống không, trả về 200 kèm body ApiResponse rõ ràng để báo trạng thái cho Frontend dễ xử lý báo Toast message.
        return ResponseEntity.ok(ApiResponse.success("Xóa chuyên khoa thành công", null));
    }
}