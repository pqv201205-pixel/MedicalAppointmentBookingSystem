package com.example.demo.Services;


import com.example.demo.DTOs.RequestDTO.BookAppointmentRequest;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.Enums.AppointmentStatus;

import java.util.List;

public interface AppointmentService {

    /**
     * Thực hiện đặt lịch khám mới, trừ slot trống và sinh mã QR tự động
     */
    AppointmentResponse bookAppointment(BookAppointmentRequest request);

    /**
     * Cập nhật trạng thái vòng đời của một lịch hẹn (PENDING, CONFIRMED, COMPLETED...)
     */
    AppointmentResponse updateStatus(Integer appointmentId, AppointmentStatus status);

    /**
     * Hủy lịch hẹn và giải phóng slot khám trống cho bác sĩ
     */
    AppointmentResponse cancelAppointment(Integer appointmentId, String reason);

    /**
     * Lấy danh sách lịch sử khám bệnh của một Bệnh nhân (Sắp xếp theo ngày mới nhất)
     */
    List<AppointmentResponse> getPatientAppointments(Integer patientId);

    /**
     * Lấy danh sách lịch hẹn cần khám của một Bác sĩ
     */
    List<AppointmentResponse> getDoctorAppointments(Integer doctorId);

    AppointmentResponse getById(Integer id);
    List<AppointmentResponse> getAllAppointments();

    List<AppointmentResponse> getUpcomingAppointments(Integer patientId);
    List<AppointmentResponse> getAppointmentHistory(Integer patientId);
}