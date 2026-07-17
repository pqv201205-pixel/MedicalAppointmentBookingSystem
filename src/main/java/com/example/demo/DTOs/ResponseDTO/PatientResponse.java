package com.example.demo.DTOs.ResponseDTO;

import com.example.demo.Entities.Patient;
import lombok.*;
import java.time.LocalDate;
@NoArgsConstructor  // <--- THÊM DÒNG NÀY ĐỂ MỞ PUBLIC CONSTRUCTOR RỖNG
@AllArgsConstructor
@Data
@Builder
public class PatientResponse {
    private Integer     patientId;
    private String      fullName;
    private String      gender;
    private LocalDate   dateOfBirth;
    private String      address;
    private String      medicalHistory;
    private Integer userId;
    public static PatientResponse from(Patient patient) {
        return PatientResponse.builder()
                .patientId(patient.getPatientId())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .userId(patient.getPatientId())
                .build();
    }
}