package IIS.wis2_backend.Models.Term;

import java.sql.Date;

import jakarta.persistence.*;

/**
 * Model representing an exam.
 */
@Entity
@Table(name = "EXAMS")
public class Exam extends Term {
    /**
     * Exam date.
     */
    Date date;

    /**
     * Exam duration in minutes.
     */
    Integer duration;

    /**
     * Number of exam attempts.
     */
    Integer attempts;
}