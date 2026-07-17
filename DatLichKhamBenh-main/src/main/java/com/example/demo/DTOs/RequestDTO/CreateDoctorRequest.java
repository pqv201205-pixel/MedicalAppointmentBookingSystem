package com.example.demo.DTOs.RequestDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateDoctorRequest {
    // Thông tin tài khoản đăng nhập (User)
    private String username;
    private String password;
    private String email;

    // Thông tin hồ sơ bác sĩ (Doctor)
    private String fullName;
    private Integer specializationId; // Để tìm và gán chuyên khoa
    private String degree;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String biography;
}