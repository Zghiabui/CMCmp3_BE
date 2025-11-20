package com.example.CMCmp3.dto;

import com.example.CMCmp3.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;
}
