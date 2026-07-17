package com.example.demo.Services.Impl;

import com.example.demo.DTOs.ResponseDTO.NotificationResponse;
import com.example.demo.Entities.Notification;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Services.NotificationService;
import com.example.demo.Exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager; // Dùng để tạo nhanh Proxy User mà không cần truy vấn DB

    @Override
    @Transactional
    public void sendNotification(Integer userId, String title, String message) {
        log.info("Đang tạo và gửi thông báo tới User ID: {}", userId);

        // 1. Tạo proxy object User dựa trên ID nhận vào (Tránh câu lệnh SELECT User không cần thiết)
        User userProxy = entityManager.getReference(User.class, userId);

        // 2. Lưu thông báo vào Database theo đúng các trường của Entity
        Notification notification = new Notification();
        notification.setUser(userProxy);
        notification.setTitle(title); // Thêm tiêu đề để không bị lỗi null DB
        notification.setMessage(message);
        notification.setIsRead(false); // Khớp với trường private Boolean isRead trong Entity

        // Lưu vào DB (Trường createdAt đã có @PrePersist tự sinh, không cần set tay)
        notificationRepository.save(notification);

        // 3. Gửi tín hiệu Realtime qua WebSocket Broker cho Frontend
        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Integer userId) {
        log.info("Lấy lịch sử thông báo của User ID: {}", userId);

        // Khớp với hàm đã khai báo trong Repository mới
        List<Notification> list = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Integer userId) {
        log.info("Đếm thông báo chưa đọc của User ID: {}", userId);

        // Khớp với hàm count đã sửa trong Repository
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        log.info("Đánh dấu đã đọc cho Notification ID: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thông báo này."));

        notification.setIsRead(true); // Đồng bộ set trường Boolean
        notificationRepository.save(notification);
    }

    /**
     * Helper Method: Ánh xạ chuẩn từ Entity sang Response DTO an toàn.
     * Đã sửa toàn bộ getter theo đúng Entity thực tế.
     */
    private NotificationResponse convertToResponse(Notification entity) {
        NotificationResponse dto = new NotificationResponse();

        // Sửa lỗi: Đổi getId() thành getNotificationId() theo đúng khai báo Entity
        dto.setNotificationId(entity.getNotificationId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setIsRead(entity.getIsRead()); // Sửa lỗi: Gọi đúng getIsRead() của trường Boolean
        dto.setNotificationType(entity.getNotificationType());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}