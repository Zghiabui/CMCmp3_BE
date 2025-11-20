package com.example.CMCmp3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // These are prefixes for messages that are routed to message-handling methods in @Controller classes.
        registry.setApplicationDestinationPrefixes("/app");

        // Use a full-featured broker like RabbitMQ or ActiveMQ in production.
        // For simplicity, we use the simple in-memory message broker.
        // "/topic" is for broadcast messages (one-to-many).
        // "/queue" is for private messages (one-to-one).
        registry.enableSimpleBroker("/topic", "/queue");
        
        // This prefix is used for sending messages to a specific user.
        // Spring automatically routes messages with this prefix to a user-specific queue.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The "/ws" endpoint is where clients will connect to the WebSocket server.
        // withSockJS() is a fallback for browsers that don't support WebSocket.
        // setAllowedOrigins("*") allows connections from any origin, which is fine for development
        // but should be restricted in production.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
