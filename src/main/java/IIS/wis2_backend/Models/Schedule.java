package IIS.wis2_backend.Models;

import jakarta.persistence.*;

/**
 * Model representing one schedule.
 */
@Entity
@Table(name = "SCHEDULE")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}