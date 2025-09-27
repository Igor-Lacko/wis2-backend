package IIS.wis2_backend.Models.Assignment;

import java.sql.Date;

import jakarta.persistence.*;

/**
 * Abstract model representing an assignment (e.g. projects, homework).
 */
@Entity
@Table(name = "ASSIGNMENTS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Assignment {
    /**
     * Assignment ID.
     */
    private Long id;

    /**
     * Assignment deadline.
     */
    Date deadline;

    /**
     * Assignment capacity.
     */
    Integer capacity;

    /**
     * Min points you HAVE to get.
     */
    Integer minPoints;

    /**
     * Max points you can get.
     */
    Integer maxPoints;
}