package IIS.wis2_backend.Models;

import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.User;
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
    private User user;

    /**
     * Or schedule room!
     */
    @OneToOne(optional = true)
    private Room room;

    /**
     * Or even... schedule course?
     */
    @OneToOne(optional = true)
    private Course course;
}