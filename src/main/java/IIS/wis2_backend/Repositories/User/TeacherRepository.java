package IIS.wis2_backend.Repositories.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.DTO.Response.Projections.TeacherForCourseProjection;
import IIS.wis2_backend.Models.User.Teacher;

/**
 * Repository for teacher specific CRUD operations.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    /**
     * Find teachers by first or last name containing the given string.
     * 
     * @param firstName First name substring to search for.
     * @param lastName  Last name substring to search for.
     * 
     * @return List of teachers matching the criteria.
     */
    List<Teacher> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Find the course's supervising teacher.
     * 
     * @param courseId ID of the course.
     * @return The supervising teacher of the course.
     */
    TeacherForCourseProjection findFirstBySupervisedCourses_Id(Long courseId);

    /**
     * Find teachers teaching a specific course.
     * 
     * @param courseId ID of the course.
     * 
     * @return List of teachers teaching the course.
     */
    List<TeacherForCourseProjection> findByTaughtCourses_Id(Long courseId);

    /**
     * Find a teacher by their username.
     * 
     * @param username The username of the teacher.
     * 
     * @return The teacher with the given username.
     */
    Optional<Teacher> findByUsername(String username);
}