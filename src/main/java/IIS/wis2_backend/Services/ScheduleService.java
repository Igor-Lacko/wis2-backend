package IIS.wis2_backend.Services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Schedule.ScheduleItemDTO;
import IIS.wis2_backend.DTO.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.ScheduleItem;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.ScheduleItemRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Specifications.ScheduleItemSpecification;

/**
 * Service for managing schedules.
 */
@Service
public class ScheduleService {
    /**
     * User repository for user schedules.
     */
    private final UserRepository userRepository;

    /**
     * And for course schedules!
     */
    private final CourseRepository courseRepository;

    /**
     * Repository for schedule items.
     */
    private final ScheduleItemRepository scheduleItemRepository;

    /**
     * Constructor for ScheduleService.
     * 
     * @param userRepository   Repository for user schedules.
     * @param courseRepository Repository for course schedules.
     */
    public ScheduleService(UserRepository userRepository, CourseRepository courseRepository,
            ScheduleItemRepository scheduleItemRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleItemRepository = scheduleItemRepository;
    }

    /**
     * Get schedule for a user.
     * 
     * @param userId ID of the user.
     * @return ScheduleWeekDTO representing the user's schedule.
     */
    public ScheduleWeekDTO GetUserScheduleForGivenWeek(long userId, LocalDate weekStartDate) {
        Long scheduleId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getSchedule().getId();

        return GetScheduleItemsForGivenWeek(scheduleId, weekStartDate);
    }

    /**
     * Get schedule for a course.
     * 
     * @param courseId ID of the course.
     * @return ScheduleWeekDTO representing the course's schedule.
     */
    public ScheduleWeekDTO GetCourseScheduleForGivenWeek(long courseId, LocalDate weekStartDate) {
        Long scheduleId = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"))
                .getSchedule().getId();

        return GetScheduleItemsForGivenWeek(scheduleId, weekStartDate);
    }

    /**
     * Get schedule items from the given schedule for the given week.
     * 
     * @param scheduleId   ID of the schedule.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the schedule items.
     */
    private ScheduleWeekDTO GetScheduleItemsForGivenWeek(long scheduleId, LocalDate weekStartDate) {
        List<ScheduleItemDTO> scheduleItems = scheduleItemRepository.findAll(
            ScheduleItemSpecification.HasScheduleIdAndIsInGivenWeek(scheduleId, Date.valueOf(weekStartDate))
        ).stream()
         .map(this::ScheduleItemToDTO)
         .toList();

        return new ScheduleWeekDTO(scheduleItems);
    }

    /**
     * Convert ScheduleItem to ScheduleItemDTO.
     * 
     * @param item Schedule item.
     * @return ScheduleItemDTO representation.
     */
    private ScheduleItemDTO ScheduleItemToDTO(ScheduleItem item) {
        return new ScheduleItemDTO(
            item.getId(),
            item.getDate().toLocalDate(),
            item.getCourseName(),
            item.getType()
        );
    }
}