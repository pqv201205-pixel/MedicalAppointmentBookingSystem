package com.example.demo.DTOs.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationResponse {

    private Integer specializationId; // Mã ID chuyên khoa lấy từ DB, dùng để định danh khi chọn bộ lọc ngoài trang chủ

    private String name;           // Tên chuyên khoa

    private String description;    // Mô tả chuyên khoa
}