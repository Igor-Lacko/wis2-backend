package IIS.wis2_backend.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.DTO.Request.SendCourseNotificationRequestDTO;
import IIS.wis2_backend.DTO.Request.SendNotificationRequestDTO;
import IIS.wis2_backend.DTO.Response.NotificationResponseDTO;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Notification.Notification;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.NotificationRepository;
import IIS.wis2_backend.Repositories.Relational.StudentCourseRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        return notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendNotification(Long senderId, SendNotificationRequestDTO request) {
        Wis2User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        Wis2User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new NotFoundException("Recipient not found"));

        Notification notification = Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .message(request.getMessage())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void sendCourseNotification(Long senderId, Long courseId, SendCourseNotificationRequestDTO request) {
        Wis2User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        // Check permissions: Sender must be ADMIN, or (TEACHER and (supervisor or in teachers list))
        boolean isAuthorized = sender.getRole() == Roles.ADMIN;
        if (!isAuthorized && sender.getRole() == Roles.TEACHER) {
            if (course.getSupervisor().getId().equals(senderId)) {
                isAuthorized = true;
            } else {
                // Check if sender is in course teachers
                isAuthorized = course.getTeachers().stream().anyMatch(t -> t.getId().equals(senderId));
            }
        }

        if (!isAuthorized) {
            throw new UnauthorizedException("You are not authorized to send notifications for this course");
        }

        List<StudentCourse> studentCourses = studentCourseRepository.findAllByCourseId(courseId);
        
        List<Notification> notifications = studentCourses.stream()
                .map(sc -> Notification.builder()
                        .sender(sender)
                        .recipient(sc.getStudent())
                        .message(request.getMessage())
                        .course(course)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new UnauthorizedException("This notification does not belong to you");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponseDTO toDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .senderId(notification.getSender().getId())
                .senderName(notification.getSender().getFirstName() + " " + notification.getSender().getLastName())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .courseId(notification.getCourse() != null ? notification.getCourse().getId() : null)
                .courseName(notification.getCourse() != null ? notification.getCourse().getName() : null)
                .build();
    }
}
