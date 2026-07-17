package com.example.demo.Repositories;

import com.example.demo.Entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> { // Sử dụng Integer cho Khóa chính ID bệnh án

    // Hàm lấy lịch sử bệnh án của bệnh nhân (Quét xuyên qua bảng Appointment để lấy đúng PatientId)
    // Sắp xếp theo ngày khám giảm dần (Mới nhất lên đầu)
    List<MedicalRecord> findByAppointment_Patient_PatientIdOrderByAppointment_AppointmentDateDesc(Integer patientId);
}