package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "DoctorSchedules") // Đặt tên bảng trong DB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleId")
    private Integer scheduleId; // Khóa chính kiểu Integer đồng bộ toàn hệ thống

    // Liên kết Many-to-One với thực thể Doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", nullable = false)
    private Doctor doctor;

    @Column(name = "DayOfWeek", nullable = false)
    private Integer dayOfWeek; // 1 = Thứ 2, ..., 7 = Chủ nhật

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime; // Giờ bắt đầu ca khám

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime; // Giờ kết thúc ca khám

    @Column(name = "MaxSlots", nullable = false)
    private Integer maxSlots;

    // THAY ĐỔI ĐOẠN NÀY: Một cấu hình lịch tuần sẽ sinh ra nhiều ngày trực thực tế trong bảng cụ thể
    @OneToMany(mappedBy = "doctorSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

//    // Mối quan hệ một lịch trình cấu hình có thể có nhiều lịch hẹn thực tế gán vào
//    @OneToMany(mappedBy = "doctorSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Appointment> appointments;
}