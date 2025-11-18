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
    @OneToOne(optional = true, mappedBy = "schedule")
    private Course course;

    /**
     * Schedule items.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "schedule_schedule_item",
        joinColumns = @JoinColumn(name = "schedule_id"),
        inverseJoinColumns = @JoinColumn(name = "schedule_item_id")
    )
    private Set<ScheduleItem> items;
}