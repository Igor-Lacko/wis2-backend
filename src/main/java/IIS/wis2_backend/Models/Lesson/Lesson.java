package IIS.wis2_backend.Models.Lesson;

import java.sql.Date;

import jakarta.persistence.*;

/**
 * Model representing an abstract lesson (e.g. lectures or labs)
 */
@Entity
@Table(name = "LESSONS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {
    /**
     * Lesson ID.
     */
    private Long id;

    /**
     * Lesson datetime.
     */
    Date datetime;

    /**
     * Lesson duration in minutes.
     */
    Integer duration;
}