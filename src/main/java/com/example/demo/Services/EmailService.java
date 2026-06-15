package com.example.demo.Services;

import com.example.demo.Entities.Appointment;

public interface EmailService {
    void sendBookingEmail(Long patientId, Appointment appointment);
    void sendCancellationEmail(Long patientId, Appointment appointment, String reason);
    void sendReminderEmail(Long patientId, Appointment appointment);
    void sendOtpEmail(String email, String otp);
}