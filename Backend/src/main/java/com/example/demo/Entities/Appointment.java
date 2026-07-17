package com.example.demo.Entities;

import com.example.demo.Enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Appointments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentId")
    private Integer appointmentId; // Thống nhất kiểu Integer cho ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientId", nullable = false)
    private Patient patient; // Đổi từ patientId thành patient để đúng bản chất Object

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleId", nullable = false)
    private Schedule schedule; // Đồng bộ liên kết lịch làm việc của bác sĩ thay vì lưu LocalDate

    @Column(name = "AppointmentDate", nullable = false)
    private LocalDate appointmentDate; // Ngày khám cụ thể

    @Column(name = "TimeSlot", length = 20, nullable = false)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDING; // Thống nhất đặt tên là status

    @Column(name = "PaymentStatus", length = 20)
    private String paymentStatus = "UNPAID";

    @Column(name = "Symptoms", length = 500)
    private String symptoms;

    @Column(name = "Notes", length = 500)
    private String notes;

    @Column(name = "CancelReason", length = 500)
    private String cancelReason; // Bổ sung trường này vì tầng Service có dùng khi hủy lịch

    @Column(name = "QrCode", columnDefinition = "TEXT")
    private String qrCode; // Bổ sung trường này để lưu chuỗi ảnh Base64 QR Code

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}