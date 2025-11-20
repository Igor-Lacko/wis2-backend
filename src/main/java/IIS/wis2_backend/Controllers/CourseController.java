package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import IIS.wis2_backend.DTO.Request.Course.CourseCreationDTO;
import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.Services.CourseService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for course access.
 */
@RestController
@RequestMapping("/courses")
public class CourseController {
    /**
     * Course service.
     */
    private final CourseService courseService;

    /**
     * Constructor for CourseController.
     * 
     * @param courseService Course service.
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Getter for all courses.
     * 
     * @param filter Course filter attributes.
     * @return list of all courses matching the criteria.
     */
    @GetMapping
    public List<LightweightCourseDTO> GetAllCourses(@ModelAttribute CourseFilter filter) {
        return courseService.GetAllCourses(filter);
    }

    /**
     * Returns the min and max price of the currently present courses.
     * 
     * @return a DTO containing the min and max price
     */
    @GetMapping("/statistics")
    public CourseStatistics GetCoursePriceStatistics() {
        return courseService.GetCoursePriceStatistics();
    }

    /**
     * Getter for a course by id.
     * 
     * @param id Course id.
     */
    @GetMapping("/{id}")
    public FullCourseDTO GetCourseById(@PathVariable long id) {
        return courseService.GetCourseById(id);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LightweightCourseDTO> getPendingCourses() {
        return courseService.getPendingCourses();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public void approveCourse(@PathVariable Long id) {
        courseService.approveCourse(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectCourse(@PathVariable Long id) {
        courseService.rejectCourse(id);
    }

    /**
     * Creates a new course.
     * 
     * @param dto         The course creation DTO.
     * @param userDetails The authenticated user details for supervisor assignment.
     * @return The created course as a lightweight DTO.
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LightweightCourseDTO> createCourse(@RequestBody @Valid CourseCreationDTO dto,
            Authentication authentication) {
        LightweightCourseDTO createdCourse = courseService.CreateCourse(dto, authentication.getName());
        return ResponseEntity.ok(createdCourse);
    }

}