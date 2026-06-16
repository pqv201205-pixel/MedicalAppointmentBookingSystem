package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate; // Import thêm LocalDate
import java.time.LocalTime;

@Entity
@Table(name = "Schedules")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleId")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", nullable = false)
    private Doctor doctor;

    // 1. THAY ĐỔI / THÊM TRƯỜNG NÀY ĐỂ HẾT LỖI .getDate()
    @Column(name = "ScheduleDate", nullable = false)
    private LocalDate date; // Định dạng YYYY-MM-DD (Ví dụ: 2026-06-16)

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "MaxSlots")
    private Integer maxSlots;

    // Nếu bạn dùng cơ chế Optimistic Locking như comment trong Service, hãy nhớ thêm trường @Version nhé:
    @Version
    private Integer version;
}