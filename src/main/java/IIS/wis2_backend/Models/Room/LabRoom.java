package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a lab room.
 */
@Entity
@Table(name = "LAB_ROOMS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabRoom extends Room {
    /**
     * If the room has access to computers or not.
     */
    private Boolean pcSupport;
}