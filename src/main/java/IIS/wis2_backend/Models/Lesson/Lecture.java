package IIS.wis2_backend.Models.Lesson;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a lecture.
 */
@Entity
@NoArgsConstructor
@SuperBuilder
public class Lecture extends Lesson {
}