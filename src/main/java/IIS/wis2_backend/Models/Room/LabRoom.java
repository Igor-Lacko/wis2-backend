package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a lab room.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LabRoom extends Room {
    /**
     * If the room has access to computers or not.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean pcSupport = false;
}