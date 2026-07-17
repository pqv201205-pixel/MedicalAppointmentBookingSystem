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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
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
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setDoctor(requestedSchedule.getDoctor());
        appointment.setSchedule(requestedSchedule);
        appointment.setSymptoms(request.getSymptoms());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        // Lưu bước đầu lấy ID tự tăng
        appointment = appointmentRepository.save(appointment);

        // 4. Sinh mã QR
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
    public AppointmentResponse updateStatus(Integer appointmentId, AppointmentStatus status) {
        log.info("Cập nhật trạng thái lịch hẹn ID {} thành: {}", appointmentId, status);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn này."));

        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return convertToResponse(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Integer appointmentId, String reason) {
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
    public List<AppointmentResponse> getPatientAppointments(Integer patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatient_PatientIdOrderByAppointmentDateDesc(patientId);
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointments(Integer doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctor_DoctorIdAndAppointmentDate(doctorId, LocalDate.now());
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getById(Integer id) {
        log.info("Đang truy vấn chi tiết lịch hẹn ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + id));

        return convertToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        log.info("Admin đang truy vấn toàn bộ danh sách lịch hẹn trong hệ thống");

        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper Method: Ánh xạ dữ liệu sạch từ Entity sang DTO cho Frontend.
     * Đã được gom gọn các trường từ cả 2 phiên bản cũ của bạn (bao gồm QR code, Giờ khám, Lý do hủy).
     */
    private AppointmentResponse convertToResponse(Appointment entity) {
        AppointmentResponse dto = new AppointmentResponse();

        // 1. Ánh xạ thông tin cơ bản của lịch hẹn (Ép kiểu ID từ Integer sang Long)
        if (entity.getAppointmentId() != null) {
            dto.setId(Long.valueOf(entity.getAppointmentId()));
        }
        dto.setDate(entity.getAppointmentDate());
        dto.setStatus(entity.getStatus()); // Giữ nguyên kiểu Enum AppointmentStatus cho đồng bộ
        dto.setSymptoms(entity.getSymptoms());
        dto.setQrCode(entity.getQrCode());
        dto.setCancelReason(entity.getCancelReason());

        // 2. Lấy thêm thông tin thời gian từ khung giờ làm việc của Bác sĩ (Schedule)
        if (entity.getSchedule() != null) {
            dto.setStartTime(entity.getSchedule().getStartTime());
            dto.setEndTime(entity.getSchedule().getEndTime());
        }

        // 3. Ánh xạ thông tin Bác sĩ (Ép kiểu ID sang Long)
        if (entity.getDoctor() != null) {
            if (entity.getDoctor().getDoctorId() != null) {
                dto.setDoctorId(Long.valueOf(entity.getDoctor().getDoctorId()));
            }
            dto.setDoctorName(entity.getDoctor().getFullName());
        }

        // 4. Ánh xạ thông tin Bệnh nhân (Ép kiểu ID sang Long)
        if (entity.getPatient() != null) {
            if (entity.getPatient().getPatientId() != null) {
                dto.setPatientId(Long.valueOf(entity.getPatient().getPatientId()));
            }
            // Trường patientName không có trong AppointmentResponse của bạn nên bỏ qua
        }

        return dto;
    }

    @Override
    public List<AppointmentResponse> getUpcomingAppointments(Integer patientId) {
        // Tìm các lịch hẹn có trạng thái PENDING hoặc CONFIRMED sắp diễn ra
        List<AppointmentStatus> activeStatuses = Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);

        // Sử dụng hàm có sẵn trong Repository của bạn:
        List<Appointment> appointments = appointmentRepository
                .findByPatient_PatientIdAndStatusInOrderByAppointmentDateDesc(patientId, activeStatuses);

        // Convert List<Appointment> sang List<AppointmentResponse> và trả về
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentHistory(Integer patientId) {
        // Tìm các lịch hẹn có trạng thái COMPLETED hoặc CANCELLED
        List<AppointmentStatus> historyStatuses = Arrays.asList(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);

        List<Appointment> appointments = appointmentRepository
                .findByPatient_PatientIdAndStatusInOrderByAppointmentDateDesc(patientId, historyStatuses);

        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
}