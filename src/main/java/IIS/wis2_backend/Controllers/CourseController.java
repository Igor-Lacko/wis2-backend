package IIS.wis2_backend.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.ModelAttributes.CourseFilter;
import IIS.wis2_backend.DTO.Response.Course.CourseStatistics;
import IIS.wis2_backend.DTO.Response.Course.FullCourseDTO;
import IIS.wis2_backend.DTO.Response.Course.LightweightCourseDTO;
import IIS.wis2_backend.DTO.Response.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Services.CourseService;
import IIS.wis2_backend.Services.Education.ScheduleService;

import org.springframework.web.bind.annotation.RequestParam;

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
     * Schedule service for getting course schedules.
     */
    private final ScheduleService scheduleService;

    /**
     * Constructor for CourseController.
     * 
     * @param courseService   Course service.
     * @param scheduleService Schedule service.
     */
    public CourseController(CourseService courseService, ScheduleService scheduleService) {
        this.courseService = courseService;
        this.scheduleService = scheduleService;
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

    /**
     * Get schedule for a course.
     * 
     * @param id             ID of the course.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the course's schedule.
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<ScheduleWeekDTO> GetSchedule(@PathVariable long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        ScheduleWeekDTO schedule = scheduleService.GetCourseScheduleForGivenWeek(id, weekStartDate);
        return ResponseEntity.ok(schedule);
    }

}