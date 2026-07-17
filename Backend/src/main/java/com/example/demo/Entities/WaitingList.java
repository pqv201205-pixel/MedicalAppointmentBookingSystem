package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "WaitingLists")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class WaitingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WaitingId")
    private Integer waitingId;

    @Column(name = "PatientId", nullable = false)
    private Integer patientId;

    // Bệnh nhân đang xếp hàng chờ cho khung giờ cố định nào của bác sĩ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleId", nullable = false)
    private Schedule schedule;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Dùng thời gian này để sắp xếp thứ tự ưu tiên FIFO
}