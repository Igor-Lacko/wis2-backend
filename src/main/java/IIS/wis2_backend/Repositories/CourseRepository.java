package IIS.wis2_backend.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.DTO.Response.Projections.CourseForTeacherProjection;
import IIS.wis2_backend.DTO.Response.Projections.LightweightCourseProjection;
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
}