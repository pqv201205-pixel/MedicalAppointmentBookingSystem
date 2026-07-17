package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateDoctorRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự") // Khớp length = 100 ở Entity
    private String fullName;

    // ĐÃ SỬA: Đổi từ String sang Integer để hứng ID từ Frontend gửi lên
    @NotNull(message = "Chuyên khoa không được để trống")
    private Integer specializationId;

    @Size(max = 50, message = "Bằng cấp tối đa 50 ký tự") // Khớp length = 50 ở Entity
    private String degree;

    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    @Max(value = 60, message = "Số năm kinh nghiệm tối đa 60 năm")
    private Integer experienceYears;

    // ĐÃ SỬA: Thêm @NotNull vì Entity cấu hình nullable = false
    @NotNull(message = "Phí tư vấn không được để trống")
    @DecimalMin(value = "0.0", message = "Phí tư vấn không được âm")
    private BigDecimal consultationFee;

    private String biography; // Entity dùng NVARCHAR(MAX) nên không cần giới hạn kích thước @Size
}