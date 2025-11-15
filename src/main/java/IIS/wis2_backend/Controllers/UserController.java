package IIS.wis2_backend.Controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IIS.wis2_backend.DTO.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.DTO.User.TeacherDTO;
import IIS.wis2_backend.Services.ScheduleService;
import IIS.wis2_backend.Services.UserService;
import org.springframework.web.bind.annotation.RequestParam;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for user-related requests.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * Service for user-related operations.
     */
    private final UserService userService;

    /**
     * Schedule service for getting user schedules.
     */
    private final ScheduleService scheduleService;

    /**
     * Constructor for UserController.
     * 
     * @param userService Service for user-related operations.
     * @param scheduleService Schedule service for getting user schedules.
     */
    public UserController(UserService userService, ScheduleService scheduleService) {
        this.userService = userService;
    }

    /**
     * Logout a user by id.
     * @param id
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<Integer>  Logout() {
        // Invalidate jwt token and http cookie
        try {
            // Create an expired cookie for the same name so the client will remove it
            ResponseCookie expiredCookie = ResponseCookie.from("JWT", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .body(0);
        } catch (Exception e) {
            // Something went wrong while creating the cookie
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1);
        }
    }

    /**
     * Getter for a public profile by user id.
     * 
     * @param id User id.
     */
    @GetMapping("/public/{id}")
    public TeacherDTO GetPublicProfile(@PathVariable long id) {
        return userService.GetTeacherPublicProfile(id);
    }

    /**
     * Get schedule for a user.
     * 
     * @param id             ID of the user.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the user's schedule.
     */
    @GetMapping("/public/{id}/schedule")
    public ResponseEntity<ScheduleWeekDTO> GetUserSchedule(@PathVariable long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        ScheduleWeekDTO schedule = scheduleService.GetUserScheduleForGivenWeek(id, weekStartDate);
        return ResponseEntity.ok(schedule);
    }
    
}