package com.example.demo.Services;

import com.example.demo.DTOs.RequestDTO.CreateDoctorRequest;
import com.example.demo.DTOs.RequestDTO.UpdateDoctorRequest;
import com.example.demo.DTOs.ResponseDTO.DoctorResponse;
import com.example.demo.Entities.Doctor;

import java.time.LocalDate;
import java.util.List;

public interface DoctorService {
    List<DoctorResponse> getDoctors(String specialty, Integer minYears);
    DoctorResponse getDoctorById(Integer id);
    DoctorResponse getDoctorByUserId(Integer userId);
    DoctorResponse updateDoctorProfile(Integer userId, Doctor updatedDoctor);
    DoctorResponse createDoctor(CreateDoctorRequest request);
    void deleteDoctor(Integer id);
    DoctorResponse updateDoctorByAdmin(Integer id, UpdateDoctorRequest request);
    List<String> getAvailableSlots(Integer doctorId, LocalDate date);

}