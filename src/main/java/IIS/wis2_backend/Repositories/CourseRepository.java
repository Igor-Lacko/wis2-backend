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
    @Query("SELECT MAX(c.price) FROM Course c WHERE c.status = 'APPROVED'")
    Double findMaxPrice();

    /**
     * Finds the minimum course price.
     * 
     * @return Minimum course price.
     */
    @Query("SELECT MIN(c.price) FROM Course c WHERE c.status = 'APPROVED'")
    Double findMinPrice();

    /**
     * Returns all courses with the given status projected as
     * LightweightCourseProjection.
     * 
     * @param status Status of the course.
     * @return List of LightweightCourseProjection.
     */
    List<LightweightCourseProjection> findAllByStatus(RequestStatus status);

    /**
     * Returns all supervised courses for a teacher with the given id and status.
     * 
     * @param teacherId ID of the teacher.
     * @param status    Status of the course.
     * @return List of courses supervised by the teacher.
     */
    List<CourseForTeacherProjection> findBySupervisor_IdAndStatus(Long teacherId, RequestStatus status);

    /**
     * Returns all courses taught by a teacher with the given id and status.
     * 
     * @param teacherId ID of the teacher.
     * @param status    Status of the course.
     * @return List of courses taught by the teacher.
     */
    List<CourseForTeacherProjection> findByTeachers_IdAndStatus(Long teacherId, RequestStatus status);

    /**
     * Returns all courses supervised by {username} with the given status.
     * 
     * @param username Username of the supervisor.
     * @param status   Status of the course.
     * @return List of courses supervised by the user.
     */
    List<OverviewCourseProjection> findBySupervisor_UsernameAndStatus(String username, RequestStatus status);

    /**
     * Returns all courses taught by {username} with the given status.
     * 
     * @param username Username of the teacher.
     * @param status   Status of the course.
     * @return List of courses taught by the user.
     */
    List<OverviewCourseProjection> findByTeachers_UsernameAndStatus(String username, RequestStatus status);

    /**
     * Returns all courses in which {username} is enrolled with the given status.
     * 
     * @param username Username of the student.
     * @param status   Status of the course.
     * @return List of courses in which the user is enrolled.
     */
    List<OverviewCourseProjection> findDistinctByStudentCourses_Student_UsernameAndStatus(String username,
            RequestStatus status);

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

    /**
     * Returns all courses in which {username} is enrolled with the given status
     * projected as LightweightCourseProjection.
     * 
     * @param username Name of the student.
     * @param status   Probably APPROVED
     * @return List of LightweightCourseProjection.
     */
    List<LightweightCourseProjection> findLightweightDistinctByStudentCourses_Student_UsernameAndStatus(String username,
            RequestStatus status);

    /**
     * Finds courses by supervisor's username and status as a lightweight projection.
     * 
     * @param username Username of the supervisor.
     * @param status   Status of the course.
     * @return List of LightweightCourseProjection.
     */
    List<LightweightCourseProjection> findLightweightBySupervisor_UsernameAndStatus(String username, RequestStatus status);

    /**
     * Finds courses by teacher's username and status as a lightweight projection.
     * 
     * @param username Username of the teacher.
     * @param status   Status of the course.
     * @return List of LightweightCourseProjection.
     */
    List<LightweightCourseProjection> findLightweightByTeachers_UsernameAndStatus(String username, RequestStatus status);
}