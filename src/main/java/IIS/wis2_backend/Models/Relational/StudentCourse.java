package IIS.wis2_backend.Models.Relational;

import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a relation student --> studentcourse <-- course
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourse {
    /**
     * Relation ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Wis2User student;

    /**
     * The course in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Course course;

    /**
     * Number of points the student has earned in this course.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    /**
     * If the student has earned the unit credit for this course. Can be nullable (exam-only courses).
     */
    private Boolean unitCredit;

    /**
     * If the student has passed the exam for this course. Also can be nullable.
     */
    private Boolean examPassed;
    
    /**
     * Final grade for the course. Can also be nullable (non-classified unit-credit courses).
     */
    private Double finalGrade;

    /**
     * If the student has completed the course.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    /**
     * If the student has failed the course. Separate from !completed, can be due to different factors.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean failed = false;

    /**
     * Status of the student's request to join the course.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;
}