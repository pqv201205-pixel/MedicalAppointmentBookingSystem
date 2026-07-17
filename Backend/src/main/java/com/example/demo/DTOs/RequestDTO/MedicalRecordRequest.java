package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRecordRequest {

    @NotNull(message = "Mã lịch hẹn không được để trống")
    private Integer appointmentId; // Thống nhất kiểu Integer đồng bộ với AppointmentId

    @NotBlank(message = "Chẩn đoán bệnh không được để trống")
    @Size(max = 1000, message = "Chẩn đoán tối đa 1000 ký tự")
    private String diagnosis;

    @NotBlank(message = "Đơn thuốc không được để trống")
    @Size(max = 2000, message = "Đơn thuốc tối đa 2000 ký tự")
    private String prescription;

    @Size(max = 1000, message = "Ghi chú của bác sĩ tối đa 1000 ký tự")
    private String doctorNotes;
}