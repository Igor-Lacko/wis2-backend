package IIS.wis2_backend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.DTO.Response.Projections.CourseForTeacherProjection;
import IIS.wis2_backend.DTO.Response.Projections.LightweightCourseProjection;
import IIS.wis2_backend.DTO.Response.Projections.OverviewCourseProjection;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Enum.RequestStatus;

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
     * Retrieves a course by its shortcut.
     * 
     * @param shortcut Shortcut of the course.
     * @return Course with the given shortcut.
     */
    Optional<Course> findByShortcut(String shortcut);

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

    /**
     * Returns all courses projected as LightweightCourseProjection.
     * 
     * @return List of LightweightCourseProjection.
     */
    List<LightweightCourseProjection> findAllBy();

    /**
     * Returns all supervised courses for a teacher with the given id.
     * 
     * @param teacherId ID of the teacher.
     * @return List of courses supervised by the teacher.
     */
    List<CourseForTeacherProjection> findBySupervisor_Id(Long teacherId);

    /**
     * Returns all courses taught by a teacher with the given id.
     * 
     * @param teacherId ID of the teacher.
     * @return List of courses taught by the teacher.
     */
    List<CourseForTeacherProjection> findByTeachers_Id(Long teacherId);

    /**
     * Returns all courses supervised by {username}
     * 
     * @param username Username of the supervisor.
     * @return List of courses supervised by the user.
     */
    List<OverviewCourseProjection> findBySupervisor_Username(String username);

    /**
     * Returns all courses taught by {username}
     * 
     * @param username Username of the teacher.
     * @return List of courses taught by the user.
     */
    List<OverviewCourseProjection> findByTeachers_Username(String username);

    /**
     * Returns all courses in which {username} is enrolled
     * 
     * @param username Username of the student.
     * @return List of courses in which the user is enrolled.
     */
    List<OverviewCourseProjection> findDistinctByStudentCourses_Student_Username(String username);

    /**
     * Returns all courses with the given status.
     * 
     * @param status Status of the course.
     * @return List of courses with the given status.
     */
    List<Course> findByStatus(RequestStatus status);

    /**
     * Returns the count of courses with the given status.
     * 
     * @param status Status of the course.
     * @return Count of courses with the given status.
     */
    long countByStatus(RequestStatus status);
}