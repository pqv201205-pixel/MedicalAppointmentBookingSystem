package com.example.demo.DTOs.ResponseDTO;

import com.example.demo.Entities.DoctorSchedule;
import com.example.demo.Entities.Schedule;
import lombok.*;
import java.time.LocalTime;

@Data
@Builder
public class DoctorScheduleResponse {
    private Integer   scheduleId;
    private Integer   doctorId;
    private String    doctorName;
    private Integer   dayOfWeek;      // 1=Thứ 2 ... 7=Chủ nhật
    private String    dayOfWeekLabel; // "Thứ 2", "Thứ 3" ...
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer   maxSlots;

    public static DoctorScheduleResponse from(DoctorSchedule s) { // Đổi từ Schedule sang DoctorSchedule
        return DoctorScheduleResponse.builder()
                .scheduleId(s.getScheduleId())
                .doctorId(s.getDoctor().getDoctorId())
                .doctorName(s.getDoctor().getFullName()) // Đổi từ getFullName() thành getName()
                .dayOfWeek(s.getDayOfWeek())
                .dayOfWeekLabel(dayLabel(s.getDayOfWeek()))
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .maxSlots(s.getMaxSlots())
                .build();
    }

    private static String dayLabel(Integer day) {
        String[] labels = {"","Thứ 2","Thứ 3","Thứ 4","Thứ 5","Thứ 6","Thứ 7","Chủ nhật"};
        return (day >= 1 && day <= 7) ? labels[day] : "Không xác định";
    }
}