package com.example.demo.Services;


import com.example.demo.DTOs.RequestDTO.ScheduleRequest;
import com.example.demo.DTOs.ResponseDTO.ScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void createSchedule(ScheduleRequest request);
    List<ScheduleResponse> getAvailableSchedules(Integer doctorId, LocalDate date);
    void reserveSlot(Integer scheduleId); // Hàm giữ chỗ khi đặt lịch (Nơi xử lý Lock)
    void releaseSlot(Integer scheduleId); // Hàm trả lại chỗ khi bệnh nhân hủy lịch
    List<ScheduleResponse> getAllSchedulesByDoctorId(Integer doctorId);
    List<ScheduleResponse> getSchedulesByDoctorUserId(Integer userId);
}