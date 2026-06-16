package com.example.demo.Repositories;

import com.example.demo.Entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // Lấy danh sách lịch khám của một bác sĩ dựa theo mã ID
    List<Schedule> findByDoctorId(Integer doctorId);

    // Lấy danh sách lịch khám của bác sĩ trong một ngày cụ thể
    List<Schedule> findByDoctorIdAndDate(Integer doctorId, LocalDate date);
}