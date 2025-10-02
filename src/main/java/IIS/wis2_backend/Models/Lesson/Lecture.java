package IIS.wis2_backend.Models.Lesson;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

/**
 * Model representing a lecture.
 */
@Entity
@Table(name = "LECTURES")
@NoArgsConstructor
public class Lecture extends Lesson {
}