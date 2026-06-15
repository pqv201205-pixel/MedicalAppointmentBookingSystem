package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewId")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoctorId", nullable = false)
    private Doctor doctor;

    @Column(name = "PatientId", nullable = false)
    private Long patientId;

    @Column(name = "Rating", nullable = false)
    private Integer rating; // Số sao từ 1 đến 5

    @Column(name = "Comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt = LocalDateTime.now();
}