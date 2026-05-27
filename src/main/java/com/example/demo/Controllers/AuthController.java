package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.*;
import com.example.demo.DTOs.ResponseDTO.AuthResponse;
import com.example.demo.DTOs.ResponseDTO.ApiResponse;
import com.example.demo.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request){

        AuthResponse response =
                authService.register(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Đăng ký thành công",
                        response
                )
        );
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request){

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Đăng nhập thành công",
                        response
                )
        );
    }

    // Refresh token
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request){

        AuthResponse response =
                authService.refreshToken(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Refresh token thành công",
                        response
                )
        );
    }

    // Đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request){

        authService.changePassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Đổi mật khẩu thành công",
                        null
                )
        );
    }

    // Quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request){

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Đã gửi email reset password",
                        null
                )
        );
    }

    // Reset mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request){

        authService.resetPassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Reset mật khẩu thành công",
                        null
                )
        );
    }
}