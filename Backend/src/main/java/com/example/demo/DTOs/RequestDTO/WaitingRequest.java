package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WaitingRequest {

    @NotNull(message = "Mã bệnh nhân không được để trống")
    private Integer patientId; // Đồng bộ kiểu Integer cho ID hệ thống

    @NotNull(message = "Mã lịch trình khám không được để trống")
    private Integer scheduleId; // Đồng bộ kiểu Integer cho ID hệ thống
}