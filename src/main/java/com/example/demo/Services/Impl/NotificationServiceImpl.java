package com.example.demo.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, String message) {
        // Gửi tới kênh riêng của từng user cụ thể qua WebSocket Broker
        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, message);
    }
}