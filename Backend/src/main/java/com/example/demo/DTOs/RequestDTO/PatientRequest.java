package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "Họ và tên bệnh nhân không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Giới tính phải là 'Nam', 'Nữ' hoặc 'Khác'")
    private String gender;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    // Trường này không bắt buộc validation vì bệnh nhân có thể có hoặc không có tiền sử bệnh lý
    private String medicalHistory;
}