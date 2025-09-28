package IIS.wis2_backend.Models.Lesson;

import jakarta.persistence.*;

/**
 * Model representing a lab lesson.
 */
@Entity
@Table(name = "LABS")
public class Lab extends Lesson {
    /**
     * Min lab points you HAVE to get. (Will be 0 in most cases irl i guess).
     */
    private Integer minPoints;

    /**
     * Max lab points you CAN get. Can also be 0 if the labs are just for, idk, practicing on examples.
     */
    private Integer maxPoints;

    protected Lab() {}

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }
}