package com.example.demo.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    ADMIN,
    DOCTOR,
    PATIENT;
//
//    @JsonCreator
//    public static Role fromString(String value) {
//        if (value == null) return null;
//        return Role.valueOf(value.toUpperCase()); // Tự động convert "doctor" -> "DOCTOR"
//    }
//
//    @JsonValue
//    public String toValue() {
//        return this.name().toLowerCase(); // Trả về chữ thường khi serialize ra JSON nếu muốn
//    }
}