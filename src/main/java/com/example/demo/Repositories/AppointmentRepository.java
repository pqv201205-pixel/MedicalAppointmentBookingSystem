package com.example.demo.Repositories;

import com.example.demo.Entities.Appointment;
import com.example.demo.Enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Xem lịch sử khám hoặc danh sách lịch hẹn của 1 Bệnh nhân
    List<Appointment> findByPatient_PatientIdOrderByAppointmentDateDesc(Integer patientId);

    // Bác sĩ xem danh sách lịch hẹn khám của mình trong ngày hôm nay
    List<Appointment> findByDoctor_DoctorIdAndAppointmentDate(Integer doctorId, LocalDate date);

    // Chống đặt trùng lịch: Kiểm tra xem Bệnh nhân đã có lịch nào trong khung giờ này chưa
    boolean existsByPatient_PatientIdAndAppointmentDateAndTimeSlotAndStatusNot(
            Integer patientId, LocalDate date, String timeSlot, AppointmentStatus status);

    // Chống quá tải: Đếm số lượng bệnh nhân đã đặt vào khung giờ cụ thể của 1 bác sĩ
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.doctorId = :doctorId " +
            "AND a.appointmentDate = :date AND a.timeSlot = :timeSlot AND a.status <> 'CANCELLED'")
    long countBookedSlots(@Param("doctorId") Integer doctorId,
                          @Param("date") LocalDate date,
                          @Param("timeSlot") String timeSlot);

    // Scheduler: Tìm danh sách lịch hẹn theo Ngày và Trạng thái
    List<Appointment> findByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    // Đã đồng bộ: Tìm danh sách lịch hẹn dựa theo trạng thái Enum (Phục vụ cho hàm tự động hủy lịch)
    List<Appointment> findByStatus(AppointmentStatus status);

    // Scheduler: Tìm các lịch hẹn trạng thái 'PENDING' nhưng chưa thanh toán quá 15 phút để hủy tự động
    List<Appointment> findByStatusAndPaymentStatus(AppointmentStatus status, String paymentStatus);

    // Dashboard: Đếm tổng số lịch hẹn trong hệ thống
    @Query("SELECT COUNT(a) FROM Appointment a")
    long countTotalAppointments();

    // Dashboard: Đếm số lịch bị hủy để tính Tỷ lệ hủy lịch
    long countByStatus(AppointmentStatus status);

    // Bác sĩ xem toàn bộ lịch hẹn khám của mình (Dùng Integer)
    List<Appointment> findByDoctor_DoctorId(Integer doctorId);

    // Tìm tất cả lịch hẹn của một cấu hình khung giờ làm việc
    List<Appointment> findByDoctorSchedule_ScheduleId(Integer scheduleId);
}