package com.example.demo.Configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Kích hoạt tính năng Broker tin nhắn WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đường dẫn để phía Frontend (Web/Mobile) kết nối vào WebSocket Server
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép tất cả các nguồn (CORS) kết nối tới
                .withSockJS(); // Hỗ trợ SockJS dự phòng nếu trình duyệt cũ không hỗ trợ WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cấu hình các kênh (topic) để Frontend "lắng nghe/đăng ký" nhận dữ liệu real-time
        registry.enableSimpleBroker("/topic");

        // Các đường dẫn tin nhắn gửi từ Frontend lên Server sẽ có tiền tố này (ví dụ: /app/send)
        registry.setApplicationDestinationPrefixes("/app");
    }
}