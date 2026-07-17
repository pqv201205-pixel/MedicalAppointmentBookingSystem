package com.example.demo.DTOs.RequestDTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    @NotNull(message = "Mã bác sĩ không được để trống")
    private Integer doctorId; // Đồng bộ kiểu Integer theo Interface của bạn

    @NotNull(message = "Ngày làm việc không được để trống")
    @FutureOrPresent(message = "Ngày làm việc phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate date;

    @NotNull(message = "Giờ bắt đầu ca khám không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc ca khám không được để trống")
    private LocalTime endTime;

    @NotNull(message = "Số lượng ca khám tối đa không được để trống")
    @Min(value = 1, message = "Số lượng slots tối đa phải lớn hơn hoặc bằng 1")
    private Integer maxSlots;
}