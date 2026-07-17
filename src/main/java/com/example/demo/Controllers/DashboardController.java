package com.example.demo.Controllers;

import com.example.demo.Services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Khớp với: dashboardService.getSummary() bên Frontend
     * URL: GET http://localhost:8080/api/dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        log.info("REST request lấy số liệu tổng hợp Dashboard cho Admin");

        // Gọi Service tính toán số liệu của bạn
        Map<String, Object> stats = dashboardService.getAdminStats();

        // Trả về JSON chứa các trường: totalAppointments, totalDoctors, cancelledAppointments, cancellationRate
        return ResponseEntity.ok(stats);
    }
}