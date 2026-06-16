package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.WaitingRequest;
import com.example.demo.Entities.Patient;
import com.example.demo.Entities.WaitingList;
import com.example.demo.Entities.Schedule;
import com.example.demo.Entities.Appointment;
import com.example.demo.Enums.AppointmentStatus;
import com.example.demo.Repositories.WaitingListRepository;
import com.example.demo.Repositories.ScheduleRepository;
import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Services.EmailService;
import com.example.demo.Services.QrCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingListServiceImpl {

    private final WaitingListRepository waitingListRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final QrCodeService qrCodeService;

    @Transactional
    public void addToWaitingList(WaitingRequest request) {
        log.info("Thêm bệnh nhân ID {} vào danh sách chờ của lịch khám ID {}", request.getPatientId(), request.getScheduleId());

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

        WaitingList waiting = new WaitingList();
        waiting.setPatientId(request.getPatientId());
        waiting.setSchedule(schedule);
        waiting.setCreatedAt(java.time.LocalDateTime.now());

        waitingListRepository.save(waiting);
    }

    @Transactional
    public void processWaitingList(Integer scheduleId) {
        log.info("Đang quét danh sách chờ cho lịch khám ID: {}", scheduleId);

        // Lấy người xếp hàng đầu tiên (FIFO - First In, First Out)
        List<WaitingList> waitingPeople = waitingListRepository.findByDoctorSchedule_ScheduleIdOrderByCreatedAtAsc(scheduleId);

        if (waitingPeople.isEmpty()) {
            log.info("Không có ai trong danh sách chờ của lịch khám này.");
            return;
        }

        WaitingList firstInLine = waitingPeople.get(0);
        Schedule schedule = firstInLine.getSchedule();
        // Xử lý set đối tượng Patient dựa trên PatientId lấy từ danh sách chờ
        Appointment appointment = new Appointment();
        if (firstInLine.getPatientId() != null) {
            Patient patient = new Patient();
            patient.setPatientId(firstInLine.getPatientId());
            appointment.setPatient(patient); // Truyền đúng Object Patient thay vì truyền ID số nguyên
        }
        // 1. Tạo lịch hẹn mới thế chỗ cho người vừa hủy
        appointment.setSchedule(schedule);
        appointment.setStatus(AppointmentStatus.CONFIRMED); // Tự động xác nhận duyệt luôn
        appointment.setSymptoms("Đăng ký tự động từ danh sách chờ");

        appointment = appointmentRepository.save(appointment);

        // 2. Sinh mã QR Check-in
        try {
            String qrCode = qrCodeService.generateAppointmentQrCode(appointment.getAppointmentId());
            appointment.setQrCode(qrCode);
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            log.error("Lỗi sinh QR cho khách hàng đợi", e);
        }

        // 3. Xóa người này khỏi danh sách chờ công khai
        waitingListRepository.delete(firstInLine);

        // 4. Gửi email thông báo khẩn cấp cho bệnh nhân biết họ đã có lịch hẹn thành công
        emailService.sendBookingEmail(appointment.getPatient().getPatientId(), appointment);
        log.info("Đã chuyển đổi thành công vé chờ sang lịch hẹn chính thức cho Patient ID: {}", firstInLine.getPatientId());
    }
}