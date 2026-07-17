package com.example.demo.DTOs.ResponseDTO;

import com.example.demo.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data // Tự động sinh tất cả Getter, Setter, toString, equals, hashCode chuẩn public
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse { // BẮT BUỘC phải có chữ "public" ở đây

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status; // Lưu ý kiểu Enum hay String cho đồng bộ
    private String symptoms;
    private String qrCode;
    private String cancelReason;
}