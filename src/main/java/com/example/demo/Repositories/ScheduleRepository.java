package com.example.demo.Repositories;

import com.example.demo.Entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // Lấy danh sách lịch khám của một bác sĩ dựa theo mã ID
    List<Schedule> findByDoctor_DoctorId(Integer doctorId);


    List<Schedule> findByDoctor_DoctorIdAndDate(Integer doctorId, LocalDate date);
    List<Schedule> findByDoctor_DoctorIdAndDateAndMaxSlotsGreaterThan(Integer doctorId, LocalDate date, Integer maxSlots);

    // MỚI: Tìm lịch bằng cách đi xuyên qua thuộc tính User liên kết với Doctor
    List<Schedule> findByDoctor_User_UserId(Integer userId);
}