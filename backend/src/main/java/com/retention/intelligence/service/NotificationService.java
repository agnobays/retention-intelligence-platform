package com.retention.intelligence.service;

import com.retention.intelligence.entity.Notification;
import com.retention.intelligence.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }
}
