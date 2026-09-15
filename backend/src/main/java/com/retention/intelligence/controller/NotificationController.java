package com.retention.intelligence.controller;

import com.retention.intelligence.entity.Notification;
import com.retention.intelligence.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/notifications", "/notifications"})
@Tag(name = "Notifications", description = "Endpoints for real-time SSE stream and user alert notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Unread User Notifications", description = "Returns active unread alerts for a user")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadUserNotifications(userId));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe Real-Time SSE Notification Stream", description = "Subscribes to live Server-Sent Events stream for instant alerts")
    public SseEmitter streamNotifications() {
        return notificationService.subscribeSse();
    }

    @PostMapping("/broadcast")
    @Operation(summary = "Broadcast Notification", description = "Triggers a real-time SSE broadcast alert across connected frontends")
    public ResponseEntity<Map<String, String>> broadcastNotification(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "Retention System Alert");
        String message = payload.getOrDefault("message", "A new retention event has occurred.");
        String type = payload.getOrDefault("type", "alert");

        notificationService.broadcastNotification(title, message, type);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Notification broadcasted in real time."));
    }
}
