package com.example.demo.Services.Impl;

import com.example.demo.Entities.Appointment;
import com.example.demo.Services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    // Inject JavaMailSender cũ của bạn vào đây

    @Override
    @Async // Đảm bảo việc gửi mail không làm chậm tiến trình chính
    public void sendBookingEmail(Long patientId, Appointment appointment) {
        // Logic gửi mail xác nhận đặt lịch cũ của bạn...
    }

    @Override
    @Async
    public void sendCancellationEmail(Long patientId, Appointment appointment, String reason) {
        // Logic gửi mail thông báo hủy lịch...
    }

    @Override
    @Async
    public void sendReminderEmail(Long patientId, Appointment appointment) {
        // Logic gửi mail nhắc lịch (gọi từ Scheduler)...
    }

    @Override
    @Async
    public void sendOtpEmail(String email, String otp) {
        // Logic gửi OTP quên mật khẩu...
    }
}