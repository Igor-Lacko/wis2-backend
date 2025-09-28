package IIS.wis2_backend.Models.Term;

import jakarta.persistence.*;

/**
 * Model representing an exam.
 */
@Entity
@Table(name = "EXAMS")
public class Exam extends Term {
    /**
     * Number of exam attempts.
     */
    Integer attempts;

    protected Exam() {}

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
}