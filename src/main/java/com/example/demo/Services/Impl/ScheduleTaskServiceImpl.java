package com.example.demo.Services.Impl;

import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.AppointmentStatus;
import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleTaskServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    // Chạy vào lúc 07:00:00 hàng ngày để nhắc nhở bệnh nhân có lịch hẹn trong ngày hôm nay
    @Scheduled(cron = "0 0 7 * * ?")
    public void remindAppointmentsDaily() {
        log.info("Chạy tác vụ tự động gửi email nhắc nhở lịch khám hàng ngày...");

        List<Appointment> todayAppointments = appointmentRepository
                .findByScheduleDateAndStatus(LocalDate.now(), AppointmentStatus.CONFIRMED);

        for (Appointment appointment : todayAppointments) {
            try {
                emailService.sendReminderEmail(appointment.getPatientId(), appointment);
            } catch (Exception e) {
                log.error("Lỗi gửi email nhắc nhở tự động cho Appointment ID: {}", appointment.getId(), e);
            }
        }
    }

    // Chạy định kỳ mỗi 15 phút một lần để hủy các lịch hẹn PENDING mà người dùng bỏ quên, quá giờ khám
    @Scheduled(fixedDelay = 900000)
    @Transactional
    public void autoCancelExpiredAppointments() {
        log.info("Đang kiểm tra và tự động dọn dẹp các lịch hẹn hết hạn đăng ký...");

        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING);
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        for (Appointment app : pendingAppointments) {
            // Nếu ngày khám đã qua, hoặc trong ngày hôm nay nhưng quá khung giờ khám bắt đầu
            if (app.getSchedule().getDate().isBefore(today) ||
                    (app.getSchedule().getDate().isEqual(today) && app.getSchedule().getStartTime().isBefore(now))) {

                app.setStatus(AppointmentStatus.CANCELLED);
                app.setCancelReason("Hệ thống tự động hủy do quá hạn xác nhận hoặc không đến quầy Check-in.");
                appointmentRepository.save(app);

                log.info("Đã tự động hủy lịch hẹn ID: {}", app.getId());
            }
        }
    }
}