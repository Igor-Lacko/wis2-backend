package IIS.wis2_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Course;

/**
 * Repository for course CRUD operations.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    /**
     * Checks if a course exists by its shortcut.
     * 
     * @param shortcut Shortcut of the course.
     * @return true if the course exists, false otherwise.
     */
    boolean existsByShortcut(String shortcut);

    /**
     * Finds the maximum course price.
     * 
     * @return Maximum course price.
     */
    @Query("SELECT MAX(c.price) FROM Course c")
    Double findMaxPrice();

    /**
     * Finds the minimum course price.
     * 
     * @return Minimum course price.
     */
    @Query("SELECT MIN(c.price) FROM Course c")
    Double findMinPrice();
}