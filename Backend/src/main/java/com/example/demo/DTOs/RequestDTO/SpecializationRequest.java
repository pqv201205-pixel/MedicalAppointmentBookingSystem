package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationRequest {

    @NotBlank(message = "Tên chuyên khoa không được phép để trống")
    @Size(min = 2, max = 100, message = "Tên chuyên khoa phải có độ dài từ 2 đến 100 ký tự")
    private String name; // Ví dụ: Tim mạch, Da liễu, Nhi khoa...

    @NotBlank(message = "Mô tả chuyên khoa không được phép để trống")
    @Size(max = 500, message = "Mô tả chuyên khoa không được vượt quá 500 ký tự")
    private String description; // Mô tả tổng quan về chuyên khoa để hiển thị trên ứng dụng
}