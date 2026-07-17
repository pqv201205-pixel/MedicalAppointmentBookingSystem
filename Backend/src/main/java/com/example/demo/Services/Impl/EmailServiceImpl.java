package com.example.demo.Services.Impl;

import com.example.demo.Entities.Appointment;
import com.example.demo.Services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "no-reply@ykhoa.com"; // Thay bằng email hệ thống của bạn
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Async // Chạy bất đồng bộ, không làm nghẽn luồng xử lý chính khi người dùng đặt lịch
    public void sendBookingEmail(Integer patientId, Appointment appointment) {
        try {
            String toEmail = appointment.getPatient().getUser().getEmail();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject("🏥 XÁC NHẬN ĐẶT LỊCH KHÁM BỆNH THÀNH CÔNG");

            String htmlContent = String.format(
                    "<h3>Xin chào %s,</h3>" +
                            "<p>Lịch hẹn khám bệnh của bạn đã được ghi nhận trên hệ thống với thông tin như sau:</p>" +
                            "<ul>" +
                            "<li><b>Mã lịch hẹn:</b> #%d</li>" +
                            "<li><b>Bác sĩ khám:</b> %s</li>" +
                            "<li><b>Ngày khám:</b> %s</li>" +
                            "<li><b>Khung giờ:</b> %s</li>" +
                            "</ul>" +
                            "<p>Vui lòng đến trước 15 phút tại quầy Check-in để hoàn tất thủ tục khám bệnh.</p>" +
                            "<p>Trân trọng,<br>Hệ thống Đặt lịch Y khoa</p>",
                    appointment.getPatient().getFullName(),
                    appointment.getAppointmentId(),
                    appointment.getDoctor().getFullName(),
                    appointment.getAppointmentDate().format(DATE_FORMATTER),
                    appointment.getTimeSlot()
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi email xác nhận đặt lịch cho Appointment ID: {}", appointment.getAppointmentId());
        } catch (MessagingException e) {
            log.error("Lỗi gửi email xác nhận đặt lịch cho Appointment ID: {}", appointment.getAppointmentId(), e);
        }
    }

    @Override
    @Async
    public void sendCancellationEmail(Integer patientId, Appointment appointment, String reason) {
        try {
            String toEmail = appointment.getPatient().getUser().getEmail();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject("❌ THÔNG BÁO HỦY LỊCH HẸN KHÁM BỆNH");

            String htmlContent = String.format(
                    "<h3>Xin chào %s,</h3>" +
                            "<p>Chúng tôi rất tiếc phải thông báo lịch hẹn khám bệnh mã <b>#%d</b> của bạn đã bị hủy.</p>" +
                            "<p><b>Lý do hủy:</b> %s</p>" +
                            "<p>Nếu có bất kỳ thắc mắc nào hoặc muốn đặt lại lịch khám mới, vui lòng truy cập lại vào ứng dụng của chúng tôi.</p>" +
                            "<p>Trân trọng,<br>Hệ thống Đặt lịch Y khoa</p>",
                    appointment.getPatient().getFullName(),
                    appointment.getAppointmentId(),
                    reason
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi email thông báo hủy lịch cho Appointment ID: {}", appointment.getAppointmentId());
        } catch (MessagingException e) {
            log.error("Lỗi gửi email hủy lịch cho Appointment ID: {}", appointment.getAppointmentId(), e);
        }
    }

    @Override
    @Async
    public void sendReminderEmail(Integer patientId, Appointment appointment) {
        try {
            String toEmail = appointment.getPatient().getUser().getEmail();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject("⏰ NHẮC NHỞ: BẠN CÓ LỊCH HẸN KHÁM BỆNH TRONG NGÀY HÔM NAY");

            String htmlContent = String.format(
                    "<h3>Xin chào %s,</h3>" +
                            "<p>Đây là thư nhắc nhở tự động từ hệ thống. Bạn có lịch hẹn khám bệnh vào ngày hôm nay:</p>" +
                            "<ul>" +
                            "<li><b>Bác sĩ đảm nhiệm:</b> %s</li>" +
                            "<li><b>Khung giờ khám:</b> %s</li>" +
                            "</ul>" +
                            "<p>Vui lòng mang theo căn cước công dân hoặc thẻ bảo hiểm y tế (nếu có) khi đến quầy Check-in khám bệnh.</p>" +
                            "<p>Chúc bạn một ngày nhiều sức khỏe!<br>Hệ thống Đặt lịch Y khoa</p>",
                    appointment.getPatient().getFullName(),
                    appointment.getDoctor().getFullName(),
                    appointment.getTimeSlot()
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi email nhắc lịch tự động thành công cho Appointment ID: {}", appointment.getAppointmentId());
        } catch (MessagingException e) {
            log.error("Lỗi gửi email nhắc lịch tự động cho Appointment ID: {}", appointment.getAppointmentId(), e);
        }
    }

    @Override
    @Async
    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(email);
            helper.setSubject("🔐 MÃ OTP XÁC THỰC TÀI KHOẢN (CÓ HIỆU LỰC 5 PHÚT)");

            String htmlContent = String.format(
                    "<h3>Yêu cầu mã xác thực OTP,</h3>" +
                            "<p>Bạn (hoặc ai đó) vừa yêu cầu cấp mã xác thực cho tài khoản liên kết với email này.</p>" +
                            "<p>Mã OTP bảo mật của bạn là: <h2 style='color: #2196F3; letter-spacing: 2px;'>%s</h2></p>" +
                            "<p><i>Lưu ý: Tuyệt đối không chia sẻ mã này với bất kỳ ai để bảo vệ an toàn cho tài khoản. Mã sẽ hết hạn sau 5 phút.</i></p>" +
                            "<p>Thân ái,<br>Hệ thống Đặt lịch Y khoa</p>",
                    otp
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Đã gửi mã OTP thành công tới địa chỉ email: {}", email);
        } catch (MessagingException e) {
            log.error("Lỗi trong quá trình gửi mã OTP tới email: {}", email, e);
        }
    }
}