package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

/**
 * Model representing a lecture room.
 */
@Entity
@Table(name = "LECTURE_ROOMS")
@NoArgsConstructor
public class LectureRoom extends Room {
}