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
    @Column(unique = true, nullable = false)
    private String shortcut;

    /**
     * Room building.
     */
    @Column(nullable = false)
    private String building;

    /**
     * Room floor. Is a string ("basement, etc.")
     */
    @Column(nullable = false)
    private String floor;
}