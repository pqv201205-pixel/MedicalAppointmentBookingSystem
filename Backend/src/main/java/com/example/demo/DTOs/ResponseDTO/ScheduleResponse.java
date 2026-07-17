package com.example.demo.DTOs.ResponseDTO;

import com.example.demo.Entities.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
        // Tạo getter, setter, toString...
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private Integer scheduleId;      // Mã ID của lịch trình (Khớp kiểu Integer)
    private Integer doctorId;        // Mã ID bác sĩ
    private String doctorName;       // Tên hiển thị của bác sĩ phụ trách ca
    private LocalDate date;          // Ngày khám
    private LocalTime startTime;     // Giờ bắt đầu ca
    private LocalTime endTime;       // Giờ kết thúc ca
    private Integer maxSlots;        // Tổng số chỗ ban đầu
    private Integer availableSlots;  // Số lượng chỗ trống thực tế còn lại (sau khi đã trừ đi số ca đã đặt)
    public static ScheduleResponse from(Schedule schedule) {
        if (schedule == null) return null;

        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .doctorId(schedule.getDoctor() != null ? schedule.getDoctor().getDoctorId() : null)
                .doctorName(schedule.getDoctor() != null ? schedule.getDoctor().getFullName() : null)
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .maxSlots(schedule.getMaxSlots())
                .build();
    }
}
