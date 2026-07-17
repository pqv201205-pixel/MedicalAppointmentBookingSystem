package com.example.demo.Services;

import com.example.demo.DTOs.ResponseDTO.NotificationResponse;
import java.util.List;

public interface NotificationService {
    void sendNotification(Integer userId, String title, String message);

    List<NotificationResponse> getNotificationsForUser(Integer userId);
    long getUnreadCount(Integer userId);
    void markAsRead(Integer notificationId);
}