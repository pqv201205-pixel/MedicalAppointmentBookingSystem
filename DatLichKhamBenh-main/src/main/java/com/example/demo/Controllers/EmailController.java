package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.OtpRequest;
import com.example.demo.Services.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    /**
     * API Yêu cầu gửi mã OTP về Email (Phục vụ Đăng ký / Quên mật khẩu)
     * URL: POST http://localhost:8080/api/email/send-otp
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody OtpRequest request) {
        log.info("Nhận yêu cầu gửi mã OTP đến email: {}", request.getEmail());

        // 1. Tự động sinh mã OTP ngẫu nhiên gồm 6 chữ số
        String generatedOtp = String.format("%06d", new Random().nextInt(1000000));

        // 2. Gọi sang EmailService xử lý gửi ngầm (Async) cho người dùng
        emailService.sendOtpEmail(request.getEmail(), generatedOtp);

        /*
         * 3. XỬ LÝ LƯU TRỮ OTP (Gợi ý logic thực tế):
         * Ở đây bạn cần lưu 'generatedOtp' này vào Redis hoặc một bảng 'OtpVerification' trong DB
         * kèm theo thời gian hết hạn (5 phút) để đối chiếu khi người dùng bấm "Xác nhận/Verify".
         */

        // 4. Trả về phản hồi thành công cho Frontend yên tâm hiển thị giao diện nhập OTP
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mã OTP đã được gửi thành công. Vui lòng kiểm tra hộp thư!");

        // CHỈ DÙNG KHI TEST: Bạn có thể trả về otp trong JSON để test Postman không cần mở email
        // response.put("debug_otp", generatedOtp);

        return ResponseEntity.ok(response);
    }
}