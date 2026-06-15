package com.example.demo.Services.Impl;

import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Repositories.DoctorRepository;
import com.example.demo.Entities.AppointmentStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalAppointments = appointmentRepository.count();
        long totalDoctors = doctorRepository.count();
        long cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        double cancellationRate = totalAppointments > 0
                ? ((double) cancelledAppointments / totalAppointments) * 100
                : 0.0;

        stats.put("totalAppointments", totalAppointments);
        stats.put("totalDoctors", totalDoctors);
        stats.put("cancelledAppointments", cancelledAppointments);
        stats.put("cancellationRate", String.format("%.2f%%", cancellationRate));

        return stats;
    }
}