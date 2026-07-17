package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.PatientRequest;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.DTOs.ResponseDTO.PatientResponse;
import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.Patient;
import com.example.demo.Enums.AppointmentStatus;
import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Repositories.PatientRepository;
import com.example.demo.Services.PatientService;
import com.example.demo.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getProfileByUserId(Integer userId) {
        log.info("Lấy thông tin hồ sơ Patient cho User ID: {}", userId);
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ bệnh nhân cho tài khoản này."));
        return convertToResponse(patient);
    }

    @Override
    @Transactional
    public PatientResponse updateProfileByUserId(Integer userId, PatientRequest request) {
        log.info("Cập nhật hồ sơ Patient cho User ID: {}", userId);
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ bệnh nhân để cập nhật."));

        // Cập nhật các trường thông tin (Hỗ trợ Partial Update - Frontend truyền gì cập nhật nấy)
        if (request.getFullName() != null) patient.setFullName(request.getFullName());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getMedicalHistory() != null) patient.setMedicalHistory(request.getMedicalHistory());

        return convertToResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointments(Integer userId) {
        log.info("Lấy danh sách lịch hẹn sắp tới của Patient thuộc User ID: {}", userId);
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bệnh nhân."));

        // Lịch hẹn sắp tới: Ngày hẹn từ hôm nay trở đi VÀ trạng thái KHÁC CANCELLED, COMPLETED
        List<AppointmentStatus> activeStatuses = Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        List<Appointment> appointments = appointmentRepository
                .findByPatient_PatientIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        patient.getPatientId(), LocalDate.now(), activeStatuses);

        return appointments.stream().map(this::convertToAppointmentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentHistory(Integer userId) {
        log.info("Lấy lịch sử khám bệnh của Patient thuộc User ID: {}", userId);
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bệnh nhân."));

        // Lịch sử khám: Đã khám xong (COMPLETED) hoặc Đã bị hủy (CANCELLED) hoặc ngày hẹn đã qua
        List<AppointmentStatus> historyStatuses = Arrays.asList(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);
        List<Appointment> appointments = appointmentRepository
                .findByPatient_PatientIdAndStatusInOrderByAppointmentDateDesc(patient.getPatientId(), historyStatuses);

        return appointments.stream().map(this::convertToAppointmentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        log.info("Admin truy vấn toàn bộ danh sách bệnh nhân");
        return patientRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Integer patientId) {
        log.info("Admin truy vấn chi tiết bệnh nhân ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân với ID: " + patientId));
        return convertToResponse(patient);
    }

    // Mappers
    private PatientResponse convertToResponse(Patient entity) {
        PatientResponse dto = new PatientResponse();
        dto.setPatientId(entity.getPatientId());
        dto.setFullName(entity.getFullName());
        dto.setGender(entity.getGender());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setAddress(entity.getAddress());
        dto.setMedicalHistory(entity.getMedicalHistory());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getUserId());
        }
        return dto;
    }

    private AppointmentResponse convertToAppointmentResponse(Appointment entity) {
        // Tái sử dụng logic hàm map Appointment sang DTO mà bạn đã viết ở AppointmentService
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(Long.valueOf(entity.getAppointmentId()));
        dto.setDate(entity.getAppointmentDate());
        dto.setStatus(entity.getStatus());
        dto.setSymptoms(entity.getSymptoms());
        dto.setQrCode(entity.getQrCode());
        if (entity.getSchedule() != null) {
            dto.setStartTime(entity.getSchedule().getStartTime());
            dto.setEndTime(entity.getSchedule().getEndTime());
        }
        if (entity.getDoctor() != null) {
            dto.setDoctorId(Long.valueOf(entity.getDoctor().getDoctorId()));
            dto.setDoctorName(entity.getDoctor().getFullName());
        }
        return dto;
    }
}