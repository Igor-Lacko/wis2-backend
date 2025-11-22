package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.SendCourseNotificationRequestDTO;
import IIS.wis2_backend.DTO.Request.SendNotificationRequestDTO;
import IIS.wis2_backend.DTO.Response.NotificationResponseDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(Authentication authentication) {
        Wis2User user = getUserByAuth(authentication);
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(Authentication authentication, @Valid @RequestBody SendNotificationRequestDTO request) {
        Wis2User user = getUserByAuth(authentication);
        notificationService.sendNotification(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<Void> sendCourseNotification(Authentication authentication, @PathVariable Long courseId, @Valid @RequestBody SendCourseNotificationRequestDTO request) {
        Wis2User user = getUserByAuth(authentication);
        notificationService.sendCourseNotification(user.getId(), courseId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(Authentication authentication, @PathVariable Long id) {
        Wis2User user = getUserByAuth(authentication);
        notificationService.markAsRead(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    private Wis2User getUserByAuth(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
