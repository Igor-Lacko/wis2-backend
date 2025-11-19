package IIS.wis2_backend.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.Schedule.ScheduleRequestDTO;
import IIS.wis2_backend.DTO.Response.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Services.Education.ScheduleService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller for returning personal (only viewable by the user himself) and
 * course schedules.
 */
@RequestMapping("/schedules")
@RestController
public class ScheduleController {
    /**
     * ScheduleService to retreive schedules.
     */
    private final ScheduleService scheduleService;

    /**
     * ScheduleController constructor.
     * 
     * @param scheduleService Schedule service to retreive schedules.
     */
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Gets the course schedule for the week starting at weekStart.
     * 
     * @param weekStart The start date of the week.
     * @param courseId The ID of the course.
     * @return ScheduleWeekDTO representing the schedule for the week.
     */
    @GetMapping("/courses")
    public ScheduleWeekDTO GetScheduleForGivenWeek(@Valid @ModelAttribute ScheduleRequestDTO request) {
        return scheduleService.GetCourseScheduleForGivenWeek(request.shortcutOrUsername(), request.weekStart());
    }

}