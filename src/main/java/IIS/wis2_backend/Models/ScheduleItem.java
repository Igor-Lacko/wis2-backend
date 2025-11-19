package IIS.wis2_backend.Models;

import java.time.LocalDateTime;

import IIS.wis2_backend.Models.Term.Term;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing one schedule item (term or lesson).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItem implements Comparable<ScheduleItem> {
    /**
     * Schedule item ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Schedule item term.
     */
    @OneToOne(optional = true)
    private Term term;

    /**
     * Type of the schedule item.
     */
    @Column(nullable = false)
    private String type;

    /**
     * Always concerns a course.
     */
    @Column(nullable = false)
    private String courseName;

    /**
     * Schedule item date.
     */
    @Column(nullable = false)
    private LocalDateTime startDate;

    /**
     * Schedule item end date.
     */
    @Column(nullable = false)
    private LocalDateTime endDate;

    /**
     * Compare schedule items by start date.
     * @param other the other schedule item to compare to
     * @return comparison result
     */
    @Override
    public int compareTo(ScheduleItem other) {
        return this.startDate.compareTo(other.startDate);
    }
}