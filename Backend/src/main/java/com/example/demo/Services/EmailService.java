package com.example.demo.Services;

import com.example.demo.Entities.Appointment;

public interface EmailService {
    void sendBookingEmail(Integer patientId, Appointment appointment);
    void sendCancellationEmail(Integer patientId, Appointment appointment, String reason);
    void sendReminderEmail(Integer patientId, Appointment appointment);
    void sendOtpEmail(String email, String otp);
}