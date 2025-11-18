package IIS.wis2_backend.Services.Education;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Lesson.LabCreationDTO;
import IIS.wis2_backend.DTO.Request.Lesson.LessonCreationDTO;
import IIS.wis2_backend.DTO.Response.Lesson.LightweightLessonDTO;
import IIS.wis2_backend.Enum.LessonType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Lesson.Lab;
import IIS.wis2_backend.Models.Lesson.Lecture;
import IIS.wis2_backend.Models.Lesson.Lesson;
import IIS.wis2_backend.Models.Relational.StudentLab;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Education.Lesson.LabRepository;
import IIS.wis2_backend.Repositories.Education.Lesson.LectureRepository;
import IIS.wis2_backend.Repositories.Education.Lesson.LessonRepository;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;

/**
 * Service for managing lessons.
 */
@Service
public class LessonService {
    /**
     * Lesson repository for RUD operations.
     */
    private final LessonRepository lessonRepository;

    /**
     * Lecture repository as one of the concrete lesson types for C operations.
     */
    private final LectureRepository lectureRepository;

    /**
     * And lab repository!
     */
    private final LabRepository labRepository;

    /**
     * To fetch teachers.
     */
    private final TeacherRepository teacherRepository;

    /**
     * Anddd courses.
     */
    private final CourseRepository courseRepository;

    /**
     * And rooms!
     */
    private final RoomRepository roomRepository;

    /**
     * Service for creating schedules.
     */
    private final ScheduleService scheduleService;

    /**
     * Constructor for LessonService.
     * 
     * @param lessonRepository  Repository for lessons.
     * @param lectureRepository Repository for lectures.
     * @param labRepository     Repository for labs.
     * @param teacherRepository Repository for teachers.
     * @param roomRepository    Repository for rooms.
     * @param courseRepository  Repository for courses.
     */
    public LessonService(LessonRepository lessonRepository, LectureRepository lectureRepository,
            LabRepository labRepository, TeacherRepository teacherRepository, RoomRepository roomRepository,
            CourseRepository courseRepository, ScheduleService scheduleService) {
        this.lessonRepository = lessonRepository;
        this.lectureRepository = lectureRepository;
        this.labRepository = labRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.courseRepository = courseRepository;
        this.scheduleService = scheduleService;
    }

    /**
     * Creates a new lecture.
     * 
     * @param dto The LessonCreationDTO with attributes.
     * @return The created lecture as a lightweight DTO.
     */
    public LightweightLessonDTO CreateLecture(LessonCreationDTO dto) {
        // Needed entities
        Teacher lecturer = teacherRepository.findByUsername(dto.getLecturerUsername())
                .orElseThrow(() -> new NotFoundException("Lecturer " + dto.getLecturerUsername() + " not found"));

        Course course = courseRepository.findByShortcut(dto.getCourseShortcut())
                .orElseThrow(() -> new NotFoundException("Course " + dto.getCourseShortcut() + " not found"));

        Room room = roomRepository.findByShortcut(dto.getRoomShortcut())
                .orElseThrow(() -> new NotFoundException("Room " + dto.getRoomShortcut() + " not found"));

        // Create
        Lecture lecture = Lecture.builder()
                .dateTime(dto.getDateTime())
                .duration(dto.getDuration())
                .lecturer(lecturer)
                .course(course)
                .room(room)
                .build();

        lectureRepository.save(lecture);

        // Create a schedule item for all students in the course and the lecturer
        scheduleService.CreateScheduleForLesson(lecture, LessonType.LECTURE.name());
        return convertToLightweightDTO(lecture);
    }

    /**
     * Creates a new lab.
     * 
     * @param dto The LabCreationDTO with attributes.
     * @return The created lab as a lightweight DTO.
     */
    public LightweightLessonDTO CreateLab(LabCreationDTO dto) {
        // Needed entities (how do i not violate DRY with this :-( )
        Teacher lecturer = teacherRepository.findByUsername(dto.getLecturerUsername())
                .orElseThrow(() -> new NotFoundException("Lecturer " + dto.getLecturerUsername() + " not found"));

        Course course = courseRepository.findByShortcut(dto.getCourseShortcut())
                .orElseThrow(() -> new NotFoundException("Course " + dto.getCourseShortcut() + " not found"));

        Room room = roomRepository.findByShortcut(dto.getRoomShortcut())
                .orElseThrow(() -> new NotFoundException("Room " + dto.getRoomShortcut() + " not found"));

        Lab lab = Lab.builder()
                .dateTime(dto.getDateTime())
                .duration(dto.getDuration())
                .lecturer(lecturer)
                .course(course)
                .room(room)
                .minPoints(dto.getMinPoints())
                .maxPoints(dto.getMaxPoints())
                .build();

        // Register all --> save --> schedule
        RegisterStudentsToLab(lab);
        labRepository.save(lab);
        scheduleService.CreateScheduleForLesson(lab, LessonType.LAB.name());

        return convertToLightweightDTO(lab);
    }

    /**
     * Registers all students of the course to the given lab.
     * 
     * @param lab The lab to register students to.
     */
    private void RegisterStudentsToLab(Lab lab) {
        Course course = lab.getCourse();
        Set<StudentLab> students = new HashSet<StudentLab>();
        course.getStudentCourses().forEach(sc -> {
            Student student = sc.getStudent();
            StudentLab studentLab = StudentLab.builder()
                    .student(student)
                    .lab(lab)
                    .build();
            students.add(studentLab);
        });

        lab.setStudents(students);
    }


    /**
     * Converts a created Lesson to a LightweightLessonDTO.
     * 
     * @param lesson The lesson to convert.
     */
    private LightweightLessonDTO convertToLightweightDTO(Lesson lesson) {
        return new LightweightLessonDTO(
                lesson.getId(),
                lesson.getDateTime(),
                lesson.getDuration(),
                lesson.getRoom().getShortcut());
    }
}