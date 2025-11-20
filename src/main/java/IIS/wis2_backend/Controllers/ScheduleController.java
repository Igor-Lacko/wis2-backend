package IIS.wis2_backend.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Request.Schedule.ScheduleRequestDTO;
import IIS.wis2_backend.DTO.Response.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
import IIS.wis2_backend.Services.Education.ScheduleService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
     * @param courseId  The ID of the course.
     * @return ScheduleWeekDTO representing the schedule for the week.
     */
    @GetMapping("/courses")
    public ResponseEntity<ScheduleWeekDTO> GetScheduleForGivenWeek(@Valid @ModelAttribute ScheduleRequestDTO request) {
        ScheduleWeekDTO dto = scheduleService.GetCourseScheduleForGivenWeek(request.shortcutOrUsername(),
                request.weekStart());
        return ResponseEntity.ok(dto);
    }

    /**
     * Gets the user schedule for the week starting at weekStart.
     * 
     * @param weekStart The start date of the week.
     * @param username  The username of the user.
     * @return ScheduleWeekDTO representing the schedule for the week.
     */
    @GetMapping("/users")
    public ResponseEntity<ScheduleWeekDTO> GetUserScheduleForGivenWeek(
            @Valid @ModelAttribute ScheduleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (!request.shortcutOrUsername().equals(userDetails.getUsername())) {
            throw new UnauthorizedException("You can't view other users' schedules.");
        }

        ScheduleWeekDTO dto = scheduleService.GetUserScheduleForGivenWeek(request.shortcutOrUsername(),
                request.weekStart());
        return ResponseEntity.ok(dto);
    }
}