package IIS.wis2_backend.Repositories.Relational;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.Relational.StudentCourse;

/**
 * Repository for fetching student-course relationships.
 */
@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    /**
     * Fetches all StudentCourse entries for a given course shortcut with the given
     * status.
     */
    List<StudentCourse> findByCourseShortcutAndStatus(String shortcut, RequestStatus status);

    /**
     * The same, but also for a specific student username.
     */
    Optional<StudentCourse> findByCourseShortcutAndStatusAndStudentUsername(String shortcut, RequestStatus status,
            String username);

    /**
     * Fetches all StudentCourse entries for a given course ID.
     */
    List<StudentCourse> findAllByCourseId(Long courseId);
}