package IIS.wis2_backend.DTO.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private Long senderId;
    private String senderName; // e.g., "John Doe"
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private Long courseId;
    private String courseName;
}
