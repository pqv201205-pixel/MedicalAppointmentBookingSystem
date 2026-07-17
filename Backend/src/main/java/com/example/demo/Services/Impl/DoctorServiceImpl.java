package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.CreateDoctorRequest;
import com.example.demo.DTOs.RequestDTO.UpdateDoctorRequest;
import com.example.demo.DTOs.ResponseDTO.DoctorResponse;
import com.example.demo.Entities.Appointment;
import com.example.demo.Entities.Doctor;
import com.example.demo.Entities.Specialization;
import com.example.demo.Entities.User;
import com.example.demo.Enums.Role;
import com.example.demo.Repositories.AppointmentRepository;
import com.example.demo.Repositories.DoctorRepository;
import com.example.demo.Repositories.SpecializationRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.DoctorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    /**
     * Xử lý API: getDoctors(params) bên Frontend
     * Lọc bác sĩ theo Chuyên khoa và Số năm kinh nghiệm (nếu có truyền lên)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctors(String specialty, Integer minYears) {
        log.info("Tìm kiếm danh sách bác sĩ với Chuyên khoa: {}, Năm kinh nghiệm tối thiểu: {}", specialty, minYears);

        List<Doctor> doctors;

        // Trường hợp 1: Có cả chuyên khoa và yêu cầu năm kinh nghiệm tối thiểu
        if (specialty != null && !specialty.trim().isEmpty() && minYears != null) {
            doctors = doctorRepository.findBySpecialization_NameAndExperienceYearsGreaterThanEqual(specialty, minYears);
        }
        // Trường hợp 2: Chỉ lọc theo chuyên khoa
        else if (specialty != null && !specialty.trim().isEmpty()) {
            doctors = doctorRepository.findBySpecialization_Name(specialty);
        }
        // Trường hợp 3: Không truyền bộ lọc, lấy toàn bộ danh sách bác sĩ
        else {
            doctors = doctorRepository.findAll();
        }

        // Sử dụng DoctorResponse.from() để chuyển đổi danh sách Entity sang DTO
        return doctors.stream()
                .map(DoctorResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Xử lý API: getDoctorById(id) bên Frontend
     */
    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Integer id) {
        log.info("Lấy thông tin chi tiết bác sĩ theo ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ có ID: " + id));

        return DoctorResponse.from(doctor);
    }

    /**
     * Xử lý API: getMyProfile() bên Frontend dành cho tài khoản Bác sĩ
     * Lấy thông tin bác sĩ dựa trên ID của tài khoản User đang đăng nhập
     */
    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByUserId(Integer userId) {
        log.info("Lấy hồ sơ bác sĩ dựa theo User ID: {}", userId);
        Doctor doctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản user này không gắn với hồ sơ bác sĩ nào."));

        return DoctorResponse.from(doctor);
    }

    /**
     * Xử lý API: updateMyProfile(data) bên Frontend
     */
    @Override
    @Transactional
    public DoctorResponse updateDoctorProfile(Integer userId, Doctor updatedData) {
        log.info("Cập nhật thông tin hồ sơ cho bác sĩ có User ID: {}", userId);

        // 1. Tìm hồ sơ bác sĩ hiện tại trong DB
        Doctor existingDoctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ bác sĩ cần cập nhật."));

        // 2. Cập nhật các trường thông tin cho phép thay đổi từ DTO/Data truyền vào
        if (updatedData.getFullName() != null) {
            existingDoctor.setFullName(updatedData.getFullName());
        }
        if (updatedData.getDegree() != null) {
            existingDoctor.setDegree(updatedData.getDegree());
        }
        if (updatedData.getExperienceYears() != null) {
            existingDoctor.setExperienceYears(updatedData.getExperienceYears());
        }
        if (updatedData.getConsultationFee() != null) {
            existingDoctor.setConsultationFee(updatedData.getConsultationFee());
        }
        if (updatedData.getBiography() != null) {
            existingDoctor.setBiography(updatedData.getBiography());
        }

        // 3. Lưu lại vào DB và chuyển đổi trả về Response DTO
        Doctor savedDoctor = doctorRepository.save(existingDoctor);
        return DoctorResponse.from(savedDoctor);
    }

    @Override
    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        log.info("Admin tiến hành tạo tài khoản và hồ sơ bác sĩ cho: {}", request.getUsername());

        // 1. Kiểm tra trùng lặp tài khoản hoặc email trước khi tạo
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên tài khoản đã tồn tại trên hệ thống!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được đăng ký!");
        }

        // 2. Tìm thông tin Chuyên khoa (Specialization)
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Chuyên khoa có ID: " + request.getSpecializationId()));

        // 3. Tạo mới và lưu Entity User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.DOCTOR);
        user.setIsActive(true); // Kích hoạt mặc định tài khoản đăng nhập

        User savedUser = userRepository.save(user);
        log.info("Đã tạo thành công tài khoản hệ thống User ID: {}", savedUser.getUserId());

        // 4. Tạo mới và cấu hình Entity Doctor liên kết với User vừa lưu
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setFullName(request.getFullName());
        doctor.setSpecialization(specialization);
        doctor.setDegree(request.getDegree());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBiography(request.getBiography());

        // 5. Lưu hồ sơ bác sĩ xuống Database và trả về dữ liệu DTO đồng nhất
        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Đã tạo thành công hồ sơ bác sĩ ID: {}", savedDoctor.getDoctorId());

        return DoctorResponse.from(savedDoctor);
    }

    /**
     * ADMIN: Xóa mềm bác sĩ để bảo toàn toàn vẹn dữ liệu
     */
    @Override
    @Transactional
    public void deleteDoctor(Integer id) {
        log.info("Admin tiến hành ẩn/khóa tài khoản bác sĩ có ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ bác sĩ cần xử lý với ID: " + id));

        User user = doctor.getUser();
        if (user != null) {
            user.setIsActive(false);
            userRepository.save(user);
            log.info("Đã vô hiệu hóa tài khoản đăng nhập của bác sĩ");
        }
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctorByAdmin(Integer id, UpdateDoctorRequest request) {
        log.info("Admin đang cập nhật thông tin cho Bác sĩ có ID: {}", id);

        // 1. Kiểm tra bác sĩ có tồn tại không
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ bác sĩ với ID: " + id));

        // 2. Kiểm tra chuyên khoa mới có hợp lệ không
        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chuyên khoa"));

        // 3. Cập nhật thông tin tài khoản User liên kết nếu cần
        User user = doctor.getUser();
        if (user != null) {
            userRepository.save(user);
        }

        // 4. Cập nhật các thông tin chuyên môn của Bác sĩ
        doctor.setFullName(request.getFullName());
        doctor.setSpecialization(specialization);
        doctor.setDegree(request.getDegree());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBiography(request.getBiography());

        // 5. Lưu lại vào Database và trả về DTO đồng nhất
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Admin đã cập nhật thành công hồ sơ bác sĩ ID: {}", id);

        return DoctorResponse.from(updatedDoctor);
    }

    @Override
    public List<String> getAvailableSlots(Integer doctorId, LocalDate date) {
        // 1. Định nghĩa tất cả các ca khám mặc định trong một ngày của phòng khám
        List<String> allSlots = Arrays.asList(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
        );

        // 2. Lấy danh sách lịch hẹn ĐÃ ĐƯỢC ĐẶT của bác sĩ này trong ngày chỉ định từ Database
        // Lưu ý: Loại trừ các lịch hẹn đã bị hủy (CANCELLED) vì khung giờ đó có thể đặt lại được
        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctor_DoctorIdAndAppointmentDateAndStatusNot(
                        doctorId,
                        date,
                        com.example.demo.Enums.AppointmentStatus.CANCELLED // Sử dụng Enum của bạn tại đây
                );

        // 3. Trích xuất danh sách các "timeSlot" đã bị chiếm (ví dụ: ["09:00", "14:30"])
        List<String> bookedSlots = bookedAppointments.stream()
                .map(Appointment::getTimeSlot) // Giả định trường trong thực tế của bạn là getTimeSlot()
                .collect(Collectors.toList());

        // 4. Lọc bỏ các slot đã bị đặt, chỉ giữ lại các slot còn trống (Available)
        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }
}