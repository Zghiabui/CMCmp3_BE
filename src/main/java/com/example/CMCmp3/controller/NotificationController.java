package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.NotificationDTO;
import com.example.CMCmp3.dto.ReadNotificationsDTO;
import com.example.CMCmp3.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/me/notifications : Get all notifications for the current user.
     *
     * @return the list of notifications.
     */
    @GetMapping("/me/notifications")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        List<NotificationDTO> notifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * POST /api/notifications/read : Mark notifications as read.
     *
     * @param dto The request body containing the list of notification IDs.
     * @return an OK response.
     */
    @PostMapping("/notifications/read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody ReadNotificationsDTO dto) {
        notificationService.markNotificationsAsRead(dto.getNotificationIds());
        return ResponseEntity.ok().build();
    }
}
