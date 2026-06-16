package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.BookAppointmentRequest;
import com.example.demo.DTOs.ResponseDTO.AppointmentResponse;
import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.Schedule;
import com.example.demo.Enums.AppointmentStatus;
import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Repositories.ScheduleRepository;
import com.example.demo.Services.AppointmentService;
import com.example.demo.Services.QrCodeService;
import com.example.demo.Services.ScheduleService;
import com.example.demo.Services.EmailService;
import com.example.demo.Exceptions.ResourceNotFoundException;
import com.example.demo.Exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository; // Đã đổi tên repo cho đồng bộ
    private final ScheduleService scheduleService;
    private final EmailService emailService;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(BookAppointmentRequest request) {
        log.info("Bệnh nhân ID {} đang thực hiện đặt lịch khám...", request.getPatientId());

        // 1. Kiểm tra tồn tại lịch cấu hình của bác sĩ
        Schedule requestedSchedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch khám phù hợp."));

        // Kiểm tra trùng lịch hẹn đang hoạt động của bệnh nhân
        boolean isOverlapped = appointmentRepository.existsByPatient_PatientIdAndAppointmentDateAndTimeSlotAndStatusNot(
                request.getPatientId(), request.getAppointmentDate(), request.getTimeSlot(), AppointmentStatus.CANCELLED);

        if (isOverlapped) {
            throw new BadRequestException("Bạn đã có một lịch hẹn khác vào khung giờ này trong ngày.");
        }

        // 2. Trừ chỗ trống (Xử lý Concurrency qua ScheduleService)
        scheduleService.reserveSlot(request.getScheduleId());

        // 3. Khởi tạo đối tượng lịch hẹn mới
        Appointment appointment = new Appointment();
        // Giả lập map tạm thời ID (Nếu repository yêu cầu object Patient, bạn cần findById Patient trước)
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setDoctor(requestedSchedule.getDoctor());
        appointment.setSchedule(requestedSchedule);
        appointment.setSymptoms(request.getSymptoms());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        // Lưu bước đầu lấy ID tự tăng (Integer)
        appointment = appointmentRepository.save(appointment);

        // 4. Sinh mã QR (Ép kiểu từ Integer sang Long nếu hàm QR dùng Long, hoặc sửa hàm QR dùng Integer)
        try {
            String qrCodePath = qrCodeService.generateAppointmentQrCode(appointment.getAppointmentId());
            appointment.setQrCode(qrCodePath);
            appointment = appointmentRepository.save(appointment);
        } catch (Exception e) {
            log.error("Lỗi khi sinh mã QR Code cho lịch hẹn ID: {}", appointment.getAppointmentId(), e);
        }

        // 5. Gửi email
        try {
            emailService.sendBookingEmail(request.getPatientId(), appointment);
        } catch (Exception e) {
            log.error("Lỗi gửi email xác nhận cho bệnh nhân: {}", request.getPatientId(), e);
        }

        return convertToResponse(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Integer appointmentId, AppointmentStatus status) { // Đổi sang Integer
        log.info("Cập nhật trạng thái lịch hẹn ID {} thành: {}", appointmentId, status);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn này."));

        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToResponse(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Integer appointmentId, String reason) { // Đổi sang Integer
        log.warn("Yêu cầu hủy lịch hẹn ID: {}, lý do: {}", appointmentId, reason);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn cần hủy."));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Không thể hủy lịch hẹn đã hoàn thành hoặc đã bị hủy trước đó.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(reason);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        // Giải phóng slot khám
        scheduleService.releaseSlot(appointment.getSchedule().getScheduleId());

        try {
            emailService.sendCancellationEmail(appointment.getPatient().getPatientId(), appointment, reason);
        } catch (Exception e) {
            log.error("Lỗi gửi email thông báo hủy lịch.", e);
        }

        return convertToResponse(cancelledAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getPatientAppointments(Integer patientId) { // Đổi sang Integer
        List<Appointment> appointments = appointmentRepository.findByPatient_PatientIdOrderByAppointmentDateDesc(patientId);
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointments(Integer doctorId) { // Đổi sang Integer
        List<Appointment> appointments = appointmentRepository.findByDoctor_DoctorIdAndAppointmentDate(doctorId, LocalDate.now());
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse res = new AppointmentResponse();
        res.setId(Long.valueOf(appointment.getAppointmentId()));
        if (appointment.getPatient() != null) {
            res.setPatientId(Long.valueOf(appointment.getPatient().getPatientId()));
        }
        res.setDoctorId(Long.valueOf(appointment.getDoctor().getDoctorId()));
        res.setDoctorName(appointment.getDoctor().getFullName());
        res.setDate(appointment.getAppointmentDate());
        res.setStartTime(appointment.getSchedule().getStartTime());
        res.setEndTime(appointment.getSchedule().getEndTime());
        res.setStatus(appointment.getStatus());
        res.setSymptoms(appointment.getSymptoms());
        res.setQrCode(appointment.getQrCode());
        res.setCancelReason(appointment.getCancelReason());
        return res;
    }
}