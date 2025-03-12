package team4.admin.board.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import team4.admin.board.dto.NotificationDTO;
import team4.admin.board.model.Notification;
import team4.admin.board.service.NotificationService;
import team4.howard.member.model.UserBean;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    
    
    // 獲取用戶的通知列表
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<Notification> notifications = notificationService.getUserNotifications(userId, PageRequest.of(page, size));
        Page<NotificationDTO> dtoPage = notifications.map(NotificationDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    // 獲取未讀通知數量
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(0L);  // 未登入返回 0
        }
        
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    // 標記單個通知為已讀
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id,
            HttpSession session) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // 標記所有通知為已讀
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // WebSocket 測試端點
    @GetMapping("/test")
    public ResponseEntity<String> testNotification(
            @RequestAttribute("userId") Integer userId) {
        // 創建測試通知
        String destination = "/user/" + userId + "/topic/notifications";
        Map<String, Object> notification = Map.of(
            "id", 1,
            "type", "test",
            "content", "這是一條測試通知",
            "createdAt", System.currentTimeMillis()
        );
        
        // 發送通知
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/topic/notifications",
            notification
        );
        
        return ResponseEntity.ok("Test notification sent");
    }
}