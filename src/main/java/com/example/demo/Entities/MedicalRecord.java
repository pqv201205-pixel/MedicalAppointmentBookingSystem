package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MedicalRecords")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordId")
    private Long recordId;

    // Kết nối 1-1 với lịch hẹn cụ thể
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AppointmentId", nullable = false)
    private Appointment appointment;

    @Column(name = "PatientId", nullable = false)
    private Long patientId;

    @Column(name = "Diagnosis", nullable = false, columnDefinition = "TEXT")
    private String diagnosis; // Chẩn đoán bệnh

    @Column(name = "Prescription", columnDefinition = "TEXT")
    private String prescription; // Đơn thuốc

    @Column(name = "DoctorNotes", columnDefinition = "TEXT")
    private String doctorNotes; // Ghi chú của bác sĩ

    @Column(name = "AttachmentUrls", columnDefinition = "TEXT")
    private String attachmentUrls; // Lưu danh sách tên file/url tài liệu y tế, cách nhau bằng dấu ";"

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt = LocalDateTime.now();
}