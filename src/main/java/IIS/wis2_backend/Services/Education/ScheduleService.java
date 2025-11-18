package IIS.wis2_backend.Services.Education;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Response.Schedule.ScheduleItemDTO;
import IIS.wis2_backend.DTO.Response.Schedule.ScheduleWeekDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.ScheduleItem;
import IIS.wis2_backend.Models.Lesson.Lesson;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Education.ScheduleItemRepository;
import IIS.wis2_backend.Repositories.User.StudentRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;
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
     * Constructor for ScheduleService.
     * 
     * @param userRepository   Repository for user schedules.
     * @param courseRepository Repository for course schedules.
     */
    public ScheduleService(UserRepository userRepository, CourseRepository courseRepository,
            ScheduleItemRepository scheduleItemRepository, StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
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
     * @param scheduleId    ID of the schedule.
     * @param weekStartDate Start date of the week.
     * @return ScheduleWeekDTO representing the schedule items.
     */
    private ScheduleWeekDTO GetScheduleItemsForGivenWeek(long scheduleId, LocalDate weekStartDate) {
        List<ScheduleItemDTO> scheduleItems = scheduleItemRepository.findAll(
                ScheduleItemSpecification.HasScheduleIdAndIsInGivenWeek(scheduleId, Date.valueOf(weekStartDate)))
                .stream()
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
                item.getStartDate(),
                item.getEndDate(),
                item.getCourseName(),
                item.getType());
    }

    /**
     * Creates a schedule item for the given term and updates all user, room and
     * course schedules affiliated with it.
     * Called after creating a term (exam or midterm exam).
     * 
     * @param term the term to create schedules for
     * @param type the type of the term (exam or midterm)
     */
    public void CreateScheduleForTerm(Term term, String type) {
        Set<Student> students = term.getStudents();
        Course course = term.getCourse();
        Teacher supervisor = term.getSupervisor();

        LocalDateTime startDate = term.getDate();
        LocalDateTime endDate = startDate.plusMinutes(term.getDuration());

        // Create and save the item
        ScheduleItem scheduleItem = ScheduleItem.builder()
                .term(term)
                .type(type)
                .courseName(course.getName())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        scheduleItemRepository.save(scheduleItem);

        // Associate with everything
        for (Student student : students) {
            student.getSchedule().getItems().add(scheduleItem);
            studentRepository.save(student);
        }

        supervisor.getSchedule().getItems().add(scheduleItem);
        teacherRepository.save(supervisor);

        course.getSchedule().getItems().add(scheduleItem);
        courseRepository.save(course);
    }

    /**
     * Creates a schedule item for the given lesson and updates all user and
     * course schedules affiliated with it.
     * Called after creating a lesson.
     * 
     * @param lesson the lesson to create schedules for
     * @param type   the type of the lesson
     */
    public void CreateScheduleForLesson(Lesson lesson, String type, Date date) {
        Course course = lesson.getCourse();
        Set<Student> students = studentRepository.findByStudentCourses_Course_Id(course.getId());
        Teacher lecturer = lesson.getLecturer();

        // Compute start/end date
        LocalDateTime startDate = lesson.getDateTime();
        LocalDateTime endDate = startDate.plusMinutes(lesson.getDuration());

        // Create and save the item
        ScheduleItem scheduleItem = ScheduleItem.builder()
                .lesson(lesson)
                .type(type)
                .courseName(course.getName())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        scheduleItemRepository.save(scheduleItem);

        // Associate with everything
        for (Student student : students) {
            student.getSchedule().getItems().add(scheduleItem);
            studentRepository.save(student);
        }

        lecturer.getSchedule().getItems().add(scheduleItem);
        teacherRepository.save(lecturer);

        course.getSchedule().getItems().add(scheduleItem);
        courseRepository.save(course);
    }
}