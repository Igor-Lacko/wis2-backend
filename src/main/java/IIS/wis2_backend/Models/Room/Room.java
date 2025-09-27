package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;

/**
 * Abstract model representing one room on the campus.
 */
@Entity
@Table(name = "ROOMS")
@Inheritance(strategy = InheritanceType.JOINED)
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
    String shortcut;

    /**
     * Room building.
     */
    String building;

    /**
     * Room floor. Is a string ("basement, etc.")
     */
    String floor;

    /**
     * Room capacity.
     */
    Integer capacity;
}