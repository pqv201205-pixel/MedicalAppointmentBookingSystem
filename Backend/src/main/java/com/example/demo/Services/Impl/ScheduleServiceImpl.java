package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.ScheduleRequest;
import com.example.demo.DTOs.ResponseDTO.ScheduleResponse;
import com.example.demo.Entities.Doctor;
import com.example.demo.Entities.Schedule;
import com.example.demo.Repositories.DoctorRepository;
import com.example.demo.Repositories.ScheduleRepository;
import com.example.demo.Services.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public void createSchedule(ScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + request.getDoctorId()));

        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMaxSlots(request.getMaxSlots());

        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAvailableSchedules(Integer doctorId, LocalDate date) {
        log.info("Truy vấn DB lấy lịch trống của bác sĩ {} ngày {}", doctorId, date);

        // Tìm trực tiếp dưới DB những lịch khớp ngày và maxSlots > 0
        return scheduleRepository.findByDoctor_DoctorIdAndDateAndMaxSlotsGreaterThan(doctorId, date, 0)
                .stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reserveSlot(Integer scheduleId) {
        try {
            Schedule schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + scheduleId));

            if (schedule.getMaxSlots() <= 0) {
                throw new RuntimeException("Lịch khám này đã hết chỗ trống!");
            }

            // Trừ đi 1 chỗ trống
            schedule.setMaxSlots(schedule.getMaxSlots() - 1);

            // Khi lưu, Hibernate sẽ tự kiểm tra trường @Version.
            // Nếu có xung đột (2 người cùng nhấn), hệ thống sẽ ném ra ObjectOptimisticLockingFailureException
            scheduleRepository.save(schedule);

        } catch (ObjectOptimisticLockingFailureException e) {
            // Xử lý khi có xung đột đồng thời (Concurrency)
            throw new RuntimeException("Hệ thống đang bận do có nhiều người cùng đặt lịch tại slot này. Vui lòng thử lại!");
        }
    }

    @Override
    @Transactional
    public void releaseSlot(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + scheduleId));

        // Hoàn lại 1 chỗ trống khi lịch hẹn bị hủy
        schedule.setMaxSlots(schedule.getMaxSlots() + 1);

        scheduleRepository.save(schedule);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedulesByDoctorId(Integer doctorId) {
        log.info("Admin đang truy vấn toàn bộ lịch trình của Doctor ID: {}", doctorId);

        // 1. Kiểm tra bác sĩ có tồn tại không trước khi lấy lịch
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Không tìm thấy bác sĩ với ID: " + doctorId);
        }

        // 2. Lấy toàn bộ lịch, map qua DTO và trả về
        List<Schedule> schedules = scheduleRepository.findByDoctor_DoctorId(doctorId);
        return schedules.stream()
                .map(ScheduleResponse::from) // Hoặc convertToResponse tùy theo hàm bạn viết ở DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByDoctorUserId(Integer userId) {
        log.info("Bác sĩ đang truy vấn lịch trình cá nhân qua User ID: {}", userId);

        // 1. Tìm trực tiếp lịch làm việc liên kết với User ID
        List<Schedule> schedules = scheduleRepository.findByDoctor_User_UserId(userId);

        // 2. Nếu trống, check xem user này có thực sự là bác sĩ không để báo lỗi chuẩn
        if (schedules.isEmpty()) {
            log.warn("Không tìm thấy lịch trình nào hoặc User ID {} không phải tài khoản Bác sĩ hợp lệ", userId);
            // Bạn có thể trả về danh sách rỗng thay vì throw lỗi tùy thuộc vào UI mong muốn
            // return Collections.emptyList();
        }

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}