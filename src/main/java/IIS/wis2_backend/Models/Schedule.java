package IIS.wis2_backend.Models;

import java.util.Set;

import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing one schedule.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    /**
     * Schedule ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Schedule user.
     */
    @OneToOne(optional = true)
    private Wis2User user;

    /**
     * Or even... schedule course?
     */
    @OneToOne(optional = true)
    private Course course;

    /**
     * Schedule items.
     */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScheduleItem> items;
}