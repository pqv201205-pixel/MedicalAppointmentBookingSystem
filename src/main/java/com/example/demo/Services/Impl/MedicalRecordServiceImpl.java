package com.example.demo.Services.Impl;

import com.example.demo.DTOs.MedicalRecordRequest;
import com.example.demo.Entities.MedicalRecord;
import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.AppointmentStatus;
import com.example.demo.Repositories.MedicalRecordRepository;
import com.example.demo.Repositories.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Transactional
    public void createMedicalRecord(MedicalRecordRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            appointment.setStatus(AppointmentStatus.COMPLETED); // Đảm bảo trạng thái hoàn thành ca khám
            appointmentRepository.save(appointment);
        }

        MedicalRecord record = new MedicalRecord();
        record.setAppointment(appointment);
        record.setPatientId(appointment.getPatientId());
        record.setDiagnosis(request.getDiagnosis());
        record.setPrescription(request.getPrescription());
        record.setDoctorNotes(request.getDoctorNotes());

        medicalRecordRepository.save(record);
    }

    @Transactional
    public void uploadMedicalDocuments(Long recordId, MultipartFile[] files) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh án"));

        StringBuilder filePaths = new StringBuilder();

        // Xử lý tạo thư mục chứa file nếu chưa có sẵn
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) directory.mkdirs();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    String fullPath = UPLOAD_DIR + fileName;
                    file.transferTo(new File(fullPath));

                    filePaths.append(fileName).append(";");
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi trong quá trình upload tài liệu y tế", e);
                }
            }
        }
        record.setAttachmentUrls(filePaths.toString());
        medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getPatientHistory(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByAppointmentScheduleDateDesc(patientId);
    }
}