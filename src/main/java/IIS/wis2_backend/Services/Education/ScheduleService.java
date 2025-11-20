package IIS.wis2_backend.Services.Education;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Schedule.ScheduleItemDTO;
import IIS.wis2_backend.DTO.Response.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Enum.TermType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.ScheduleItem;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Education.Schedule.ScheduleItemRepository;
import IIS.wis2_backend.Repositories.Education.Schedule.ScheduleRepository;
import IIS.wis2_backend.Repositories.User.StudentRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import jakarta.transaction.Transactional;

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
     * Student to update their schedule.
     */
    private final StudentRepository studentRepository;

    /**
     * Same for teachers!
     */
    private final TeacherRepository teacherRepository;

    /**
     * And for course schedules!
     */
    private final CourseRepository courseRepository;

    /**
     * Repository for schedule items.
     */
    private final ScheduleItemRepository scheduleItemRepository;

    /**
     * And for entire schedules!
     */
    private final ScheduleRepository scheduleRepository;

    /**
     * Constructor for ScheduleService.
     * 
     * @param userRepository         Repository for user schedules.
     * @param courseRepository       Repository for course schedules.
     * @param scheduleItemRepository Repository for schedule items.
     * @param studentRepository      Repository for students.
     * @param teacherRepository      Repository for teachers.
     * @param scheduleRepository     Repository for entire schedules.
     */
    public ScheduleService(UserRepository userRepository, CourseRepository courseRepository,
            ScheduleItemRepository scheduleItemRepository, StudentRepository studentRepository,
            TeacherRepository teacherRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Get schedule for a user.
     * 
     * @param username      Username of the user.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the user's schedule.
     */
    public ScheduleWeekDTO GetUserScheduleForGivenWeek(String username, LocalDate weekStartDate) {
        if (weekStartDate.getDayOfWeek().getValue() != 1) {
            throw new IllegalArgumentException("weekStartDate must be a Monday");
        }

        Schedule schedule = scheduleRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return GetScheduleItemsForGivenWeek(schedule, weekStartDate);
    }

    /**
     * Get schedule for a course.
     * 
     * @param shortcut      Unique shortcut of the course.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the course's schedule.
     */
    public ScheduleWeekDTO GetCourseScheduleForGivenWeek(String shortcut, LocalDate weekStartDate) {
        if (weekStartDate.getDayOfWeek().getValue() != 1) {
            throw new IllegalArgumentException("weekStartDate must be a Monday");
        }

        Schedule schedule = scheduleRepository.findByCourse_Shortcut(shortcut)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        return GetScheduleItemsForGivenWeek(schedule, weekStartDate);
    }

    /**
     * Get schedule items from the given schedule for the given week.
     * 
     * @param scheduleId    ID of the schedule.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the schedule items.
     */
    private ScheduleWeekDTO GetScheduleItemsForGivenWeek(Schedule schedule, LocalDate weekStartDate) {
        LocalDateTime weekEndDate = weekStartDate.plusDays(7).atStartOfDay();
        List<ScheduleItemDTO> items = schedule.getItems()
                .stream()
                .filter(item -> !item.getEndDate().isBefore(weekStartDate.atStartOfDay()) &&
                        !item.getStartDate().isAfter(weekEndDate))
                .map(this::ScheduleItemToDTO)
                .collect(Collectors.toList());
        return new ScheduleWeekDTO(items);
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
                item.getStartDate(),
                item.getEndDate(),
                item.getCourseName(),
                item.getType());
    }

    /**
     * Creates a schedule item for the given term and updates all user and
     * course schedules affiliated with it.
     * Called after creating a term (exam or midterm exam).
     * 
     * @param term the term to create schedules for
     * @param type the type of the term
     */
    public void CreateScheduleForTerm(Term term, TermType type) {
        Set<Student> students = studentRepository.findByStudentTerms_Term_Id(term.getId());
        Course course = term.getCourse();
        Teacher supervisor = term.getSupervisor();

        LocalDateTime startDate = term.getDate();
        LocalDateTime endDate = startDate.plusMinutes(term.getDuration());

        // Create and save the item
        ScheduleItem scheduleItem = ScheduleItem.builder()
                .term(term)
                .courseName(course.getName())
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .build();

        scheduleItemRepository.save(scheduleItem);
        System.out.println("Created schedule item for term ID " + term.getId());

        // Associate with everything and persist the schedules (owner of the join table)
        for (Student student : students) {
            Schedule s = student.getSchedule();
            if (s == null)
                continue;
            s.getItems().add(scheduleItem);
            scheduleRepository.save(s);
        }

        Schedule supSchedule = supervisor.getSchedule();
        if (supSchedule != null) {
            supSchedule.getItems().add(scheduleItem);
            scheduleRepository.save(supSchedule);
        }

        Schedule courseSchedule = course.getSchedule();
        if (courseSchedule != null) {
            courseSchedule.getItems().add(scheduleItem);
            scheduleRepository.save(courseSchedule);
        }
    }
}