package com.example.demo.Enums;

public enum AppointmentStatus {

    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    COMPLETED("Đã khám xong"),
    CANCELLED("Đã hủy lịch");

    private final String description;

    // Constructor để gán mô tả Tiếng Việt
    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}