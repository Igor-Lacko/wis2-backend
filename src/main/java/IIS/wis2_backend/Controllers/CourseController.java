package IIS.wis2_backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.ModelAttributes.CourseFilter;
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
    public FullCourseDTO GetCourseById(@PathVariable Long id) {
        return courseService.GetCourseById(id);
    }
}