package com.example.demo.Controllers;

import com.example.demo.DTOs.RequestDTO.*;
import com.example.demo.DTOs.ResponseDTO.*;
import com.example.demo.Security.CustomUserDetails;

import com.example.demo.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — Xác thực & Phân quyền
 *
 * POST /api/auth/register          — Đăng ký (PUBLIC)
 * POST /api/auth/login             — Đăng nhập (PUBLIC)
 * POST /api/auth/refresh           — Làm mới token (PUBLIC)
 * POST /api/auth/logout            — Đăng xuất (cần JWT)
 * POST /api/auth/change-password   — Đổi mật khẩu (cần JWT)
 * POST /api/auth/forgot-password   — Gửi OTP quên mật khẩu (PUBLIC)
 * POST /api/auth/reset-password    — Đặt lại mật khẩu bằng OTP (PUBLIC)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ── Đăng ký ───────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Đăng ký thành công", user));
    }

    // ── Đăng nhập ─────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse auth = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", auth));
    }

    // ── Làm mới token ─────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse auth = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(auth));
    }

    // ── Đăng xuất ─────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.logout(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    // ── Đổi mật khẩu ─────────────────────────────────────────────────────────

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    // ── Quên mật khẩu — gửi OTP ──────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success("Đã gửi OTP về email " + request.getEmail(), null));
    }

    // ── Đặt lại mật khẩu bằng OTP ────────────────────────────────────────────

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công", null));
    }
}