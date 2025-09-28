package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;

/**
 * Model representing a lecture room.
 */
@Entity
@Table(name = "LECTURE_ROOMS")
public class LectureRoom extends Room {
    protected LectureRoom() {}
}