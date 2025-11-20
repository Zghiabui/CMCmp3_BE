package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Notification;
import com.example.CMCmp3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds all notifications for a specific user, ordered by creation date descending.
     * @param user The user to find notifications for.
     * @return A list of notifications.
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Marks a list of notifications as read for a specific user.
     * This query ensures that a user can only mark their own notifications as read.
     * @param notificationIds The IDs of the notifications to mark as read.
     * @param userId The ID of the user who owns the notifications.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :notificationIds AND n.user.id = :userId")
    void markAsRead(@Param("notificationIds") List<Long> notificationIds, @Param("userId") Long userId);
}
