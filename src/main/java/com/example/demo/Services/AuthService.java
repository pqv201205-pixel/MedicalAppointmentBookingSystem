package com.example.demo.Services;

import com.example.demo.DTOs.RequestDTO.*;
import com.example.demo.DTOs.ResponseDTO.AuthResponse;
import com.example.demo.Entities.User;
import com.example.demo.Enums.Role;
import com.example.demo.Exceptions.BadRequestException;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    // ================= REGISTER =================

    public AuthResponse register(RegisterRequest request){

        // kiểm tra email tồn tại
        if(userRepository.existsByEmail(request.getEmail())){

            throw new BadRequestException(
                    "Email đã tồn tại"
            );
        }

        // tạo user mới
        User user = new User();

        user.setUsername(request.getUsername());

        user.setEmail(request.getEmail());

        user.setPhoneNumber(request.getPhoneNumber());

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // mặc định PATIENT
        user.setRole(Role.PATIENT);

        // lưu database
        userRepository.save(user);

        // authenticate
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // generate token
        String accessToken =
                jwtTokenProvider.generateToken(authentication);

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(
                        user.getEmail()
                );

        // response
        return buildAuthResponse(
                user,
                accessToken,
                refreshToken
        );
    }

    // ================= LOGIN =================

    public AuthResponse login(LoginRequest request){

        // authenticate
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // lấy user
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Email không tồn tại"
                                ));

        // generate token
        String accessToken =
                jwtTokenProvider.generateToken(authentication);

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(
                        user.getEmail()
                );

        return buildAuthResponse(
                user,
                accessToken,
                refreshToken
        );
    }

    // ================= REFRESH TOKEN =================

    public AuthResponse refreshToken(
            RefreshTokenRequest request){

        String refreshToken =
                request.getRefreshToken();

        // validate refresh token
        boolean valid =
                jwtTokenProvider.validateToken(
                        refreshToken
                );

        if(!valid){

            throw new BadRequestException(
                    "Refresh token không hợp lệ"
            );
        }

        // lấy email từ token
        String email =
                jwtTokenProvider.getEmailFromToken(
                        refreshToken
                );

        // tìm user
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "User không tồn tại"
                                ));

        // tạo access token mới
        String newAccessToken =
                jwtTokenProvider.generateTokenFromEmail(
                        user.getEmail()
                );

        return buildAuthResponse(
                user,
                newAccessToken,
                refreshToken
        );
    }

    // ================= CHANGE PASSWORD =================

    public void changePassword(
            ChangePasswordRequest request){

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "User không tồn tại"
                                ));

        // check mật khẩu cũ
        boolean match =
                passwordEncoder.matches(
                        request.getOldPassword(),
                        user.getPassword()
                );

        if(!match){

            throw new BadRequestException(
                    "Mật khẩu cũ không đúng"
            );
        }

        // update password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    // ================= FORGOT PASSWORD =================

    public void forgotPassword(
            ForgotPasswordRequest request){

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Email không tồn tại"
                                ));

        // TODO:
        // generate reset token
        // send email
    }

    // ================= RESET PASSWORD =================

    public void resetPassword(
            ResetPasswordRequest request){

        // TODO:
        // validate reset token
        // update password
    }

    // ================= BUILD RESPONSE =================

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String refreshToken){

        AuthResponse response =
                new AuthResponse();

        response.setAccessToken(accessToken);

        response.setRefreshToken(refreshToken);

        response.setUserId(user.getId());

        response.setEmail(user.getEmail());

        response.setRole(user.getRole());

        return response;
    }
}