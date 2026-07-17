package com.example.demo.Services;

import com.example.demo.DTOs.RequestDTO.PatientRequest;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.DTOs.ResponseDTO.PatientResponse;
import java.util.List;

public interface PatientService {
    PatientResponse getProfileByUserId(Integer userId);
    PatientResponse updateProfileByUserId(Integer userId, PatientRequest request);
    List<AppointmentResponse> getUpcomingAppointments(Integer userId);
    List<AppointmentResponse> getAppointmentHistory(Integer userId);

    // ADMIN
    List<PatientResponse> getAllPatients();
    PatientResponse getPatientById(Integer patientId);
}