package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;
import lombok.*;

/**
 * Abstract model representing one room on the campus.
 */
@Entity
@Table(name = "ROOMS")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Room {
    /**
     * Room identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Room "tag/shortcut" (todo: maybe make this the Id? idk tbh)
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