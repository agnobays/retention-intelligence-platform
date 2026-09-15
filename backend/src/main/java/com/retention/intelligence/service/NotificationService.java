package com.retention.intelligence.service;

import com.retention.intelligence.entity.Notification;
import com.retention.intelligence.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    /**
     * Subscribe to real-time SSE notification stream
     */
    public SseEmitter subscribeSse() {
        SseEmitter emitter = new SseEmitter(0L); // 0L = Keep-alive without timeout
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try {
            Map<String, Object> welcomeEvent = new HashMap<>();
            welcomeEvent.put("id", UUID.randomUUID().toString());
            welcomeEvent.put("title", "Realtime Retention Stream Active");
            welcomeEvent.put("message", "Connected to Standard Bank Retention Intelligence SSE Emitter.");
            welcomeEvent.put("type", "success");
            welcomeEvent.put("timestamp", System.currentTimeMillis());

            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data(welcomeEvent));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    /**
     * Broadcast live notification to all connected SSE clients
     */
    public void broadcastNotification(String title, String message, String type) {
        log.info("🔔 [REAL-TIME SSE BROADCAST] {} - {}", title, message);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("title", title);
        eventData.put("message", message);
        eventData.put("type", type != null ? type : "alert");
        eventData.put("time", "Just now");
        eventData.put("read", false);
        eventData.put("timestamp", System.currentTimeMillis());

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(eventData));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
