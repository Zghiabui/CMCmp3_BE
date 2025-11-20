package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.NotificationDTO;
import com.example.CMCmp3.entity.Notification;
import com.example.CMCmp3.entity.NotificationType;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.NotificationRepository;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // INJECTED

    /**
     * Creates and saves a new notification, then sends it via WebSocket.
     * @param user The user to be notified.
     * @param type The type of the notification.
     * @param message The notification message.
     * @param link The frontend link for the notification.
     */
    @Transactional
    public void createNotification(User user, NotificationType type, String message, String link) {
        // Avoid notifying a user about their own actions
        String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if (user.getEmail().equals(currentUsername)) {
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .link(link)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Send notification via WebSocket to the specific user
        NotificationDTO dto = toDTO(savedNotification);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(), // Spring uses this to identify the user session
                "/queue/notifications", // The user-specific destination
                dto // The payload
        );
    }

    /**
     * Retrieves all notifications for the currently authenticated user.
     * @return A list of NotificationDTOs.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getMyNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marks a list of notifications as read for the currently authenticated user.
     * @param notificationIds The IDs of notifications to mark as read.
     */
    @Transactional
    public void markNotificationsAsRead(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        User currentUser = getCurrentUser();
        notificationRepository.markAsRead(notificationIds, currentUser.getId());
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getUser().getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getLink(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
