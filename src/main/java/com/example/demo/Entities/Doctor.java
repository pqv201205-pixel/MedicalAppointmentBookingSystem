package com.example.demo.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Doctors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DoctorId")
    private Integer doctorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    @JsonIgnoreProperties({"password_hash", "appointments", "doctor"}) // Bỏ qua các trường nhạy cảm hoặc gây vòng lặp
    private User user;

    @Column(name = "FullName", length = 100, nullable = false)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SpecializationId")
    @JsonIgnoreProperties({"doctors"}) // Ngăn không cho serialize ngược lại danh sách doctor từ specialization
    private Specialization specialization;

    @Column(name = "Degree", length = 50)
    private String degree;

    @Column(name = "ExperienceYears")
    private Integer experienceYears;

    @Column(name = "ConsultationFee", precision = 18, scale = 2, nullable = false)
    private BigDecimal consultationFee;

    @Column(name = "Biography", columnDefinition = "NVARCHAR(MAX)")
    private String biography;
}