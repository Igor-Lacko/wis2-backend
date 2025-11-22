package IIS.wis2_backend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.DTO.Response.Projections.CourseForTeacherProjection;
import IIS.wis2_backend.DTO.Response.Projections.LightweightCourseProjection;
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
     * Retrieves a course by its shortcut and status.
     * 
     * @param shortcut Shortcut of the course.
     * @param status   Status of the course.
     * @return Course with the given shortcut and status.
     */
    Optional<Course> findByShortcutAndStatus(String shortcut, RequestStatus status);

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
     * Generic method to find courses by supervisor's username and status with a
     * dynamic projection.
     * 
     * @param username Username of the supervisor.
     * @param status   Status of the course.
     * @param type     The class type of the projection.
     * @return List of projected courses.
     */
    <T> List<T> findBySupervisor_UsernameAndStatus(String username, RequestStatus status, Class<T> type);

    /**
     * Generic method to find courses by teacher's username and status with a
     * dynamic projection.
     * 
     * @param username Username of the teacher.
     * @param status   Status of the course.
     * @param type     The class type of the projection.
     * @return List of projected courses.
     */
    <T> List<T> findByTeachers_UsernameAndStatus(String username, RequestStatus status, Class<T> type);

    /**
     * Generic method to find courses in which {username} is enrolled with the given
     * status with a dynamic projection.
     * 
     * @param username         Username of the student.
     * @param enrollmentStatus Status of the enrollment.
     * @param courseStatus     Status of the course.
     * @param type             The class type of the projection.
     * @return List of projected courses.
     */
    @Query("SELECT DISTINCT c FROM Course c JOIN c.studentCourses sc WHERE sc.student.username = :username AND sc.status = :enrollmentStatus AND c.status = :courseStatus")
    <T> List<T> findCoursesByStudentUsernameAndStatus(@Param("username") String username,
            @Param("enrollmentStatus") RequestStatus enrollmentStatus,
            @Param("courseStatus") RequestStatus courseStatus, Class<T> type);

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
     * Returns the count of enrolled students for a course with the given shortcut.
     * 
     * @param shortcut Shortcut of the course.
     * @return Count of enrolled students.
     */
    @Query("SELECT COUNT(sc) FROM StudentCourse sc WHERE sc.course.shortcut = :shortcut AND sc.status = 'APPROVED'")
    long getEnrolledCountByCourseShortcut(@Param("shortcut") String shortcut);

    /**
     * Returns true if the user with the given username is the supervisor of the
     * course.
     * 
     * @param username Username of the user.
     * @param shortcut Shortcut of the course.
     * @return true if the user is the supervisor, false otherwise.
     */
    Boolean existsBySupervisor_UsernameAndShortcut(String username, String shortcut);

    /**
     * Returns true if the user with the given username teaches the course.
     * 
     * @param username Username of the user.
     * @param shortcut Shortcut of the course.
     * @return true if the user teaches the course, false otherwise.
     */
    Boolean existsByTeachers_UsernameAndShortcut(String username, String shortcut);

    /**
     * Returns true if the user with the given username is enrolled in the course.
     * 
     * @param username Username of the user.
     * @param shortcut Shortcut of the course.
     * @return true if the user is enrolled in the course, false otherwise.
     */
    Boolean existsByStudentCourses_Student_UsernameAndShortcut(String username, String shortcut);

    /**
     * Returns only the shortcuts of courses supervised by the given user with the
     * given status.
     * 
     * @param username Username of the supervisor.
     * @param status   Status of the course.
     * @return List of course shortcuts.
     */
    @Query("SELECT c.shortcut FROM Course c WHERE c.supervisor.username = :username AND c.status = :status")
    List<String> findShortcutsBySupervisor_UsernameAndStatus(@Param("username") String username,
            @Param("status") RequestStatus status);

    /**
     * Returns all course shortcuts the user wants to register to.
     * 
     * @param username Username of the user.
     * @return List of course shortcuts.
     */
    @Query("SELECT c.shortcut from Course c JOIN c.studentCourses sc WHERE sc.student.username = :username AND sc.status = 'PENDING'")
    List<String> findPendingCourseShortcutsByUsername(@Param("username") String username);
}