package com.example.demo.Services;

import com.example.demo.DTOs.ScheduleRequest;
import com.example.demo.DTOs.ScheduleResponse;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void createSchedule(ScheduleRequest request);
    List<ScheduleResponse> getAvailableSchedules(Long doctorId, LocalDate date);
    void reserveSlot(Long scheduleId); // Hàm giữ chỗ khi đặt lịch (Nơi xử lý Lock)
    void releaseSlot(Long scheduleId); // Hàm trả lại chỗ khi bệnh nhân hủy lịch
}