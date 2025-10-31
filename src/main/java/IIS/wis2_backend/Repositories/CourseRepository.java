package IIS.wis2_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Course;

/**
 * Repository for course CRUD operations.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    /**
     * Checks if a course exists by its shortcut.
     * 
     * @param shortcut Shortcut of the course.
     * @return true if the course exists, false otherwise.
     */
    boolean existsByShortcut(String shortcut);
}