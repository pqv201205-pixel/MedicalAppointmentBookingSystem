package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Specializations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SpecializationId")
    private Long specializationId;

    @Column(name = "Name", nullable = false, unique = true)
    private String name; // Ví dụ: Tim mạch, Da liễu...

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    // Mối quan hệ: Một chuyên khoa có nhiều bác sĩ
    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL)
    private List<Doctor> doctors;
}