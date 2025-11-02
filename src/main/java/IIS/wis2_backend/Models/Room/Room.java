package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract model representing one room on the campus.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Room {
    /**
     * Room identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Room "tag/shortcut"
     */
    private String shortcut;

    /**
     * Room building.
     */
    private String building;

    /**
     * Room floor. Is a string ("basement, etc.")
     */
    private String floor;

    /**
     * Room capacity.
     */
    private Integer capacity;
}