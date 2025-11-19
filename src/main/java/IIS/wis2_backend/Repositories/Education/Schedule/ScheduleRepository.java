package IIS.wis2_backend.Repositories.Education.Schedule;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Schedule;

/**
 * Repository for schedules.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    /**
     * Returns the schedule that belongs to the course with this shortcut.
     * 
     * @param shortcut The shortcut of the course.
     * @return An optional schedule.
     */
    Optional<Schedule> findByCourse_Shortcut(String shortcut);

    /**
     * Returns the user that belongs to the user with this username.
     * 
     * @param username The username of the user.
     * @return An optional user.
     */
    Optional<Schedule> findByUser_Username(String username);
}