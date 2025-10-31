package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.Services.CourseService;

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
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Getter for all courses.
     */
    @GetMapping
    public List<LightweightCourseDTO> GetAllCourses() {
        return courseService.GetAllCourses();
    }

    /**
     * Getter for a course by id.
     * 
     * @param id Course id.
     */
    @GetMapping("/{id}")
    public FullCourseDTO GetCourseById(@PathVariable Long id) {
        return courseService.GetCourseById(id);
    }
}