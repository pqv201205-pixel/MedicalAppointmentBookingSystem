package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookAppointmentRequest {

    // 1. Đồng bộ kiểu dữ liệu thành Long (Vì trong Service đang dùng Long cho Patient ID)
    @NotNull(message = "PatientId không được để trống")
    private Integer patientId;

    // 2. BỔ SUNG TRƯỜNG NÀY (Bắt buộc phải có để chạy hàm scheduleRepository.findById(request.getScheduleId()))
    @NotNull(message = "ScheduleId không được để trống")
    private Integer scheduleId;

    @NotNull(message = "DoctorId không được để trống")
    private Integer doctorId;

    @NotNull(message = "Ngày khám không được để trống")
    @Future(message = "Ngày khám phải là ngày trong tương lai")
    private LocalDate appointmentDate;

    @NotBlank(message = "Khung giờ không được để trống")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "Khung giờ định dạng HH:mm, ví dụ: 09:00")
    private String timeSlot;

    @Size(max = 500, message = "Triệu chứng tối đa 500 ký tự")
    private String symptoms;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String notes;
}