package IIS.wis2_backend.Services.Education;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Term.ExamCreationDTO;
import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.DTO.Response.Term.LightweightTermDTO;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.TermType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.Term.*;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.Education.Term.ExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.LabRepository;
import IIS.wis2_backend.Repositories.Education.Term.LectureRepository;
import IIS.wis2_backend.Repositories.Education.Term.MidtermExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.TermRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;

/**
 * Service for managing terms.
 */
@Service
public class TermService {
    /**
     * Term repository (generic read/delete/update).
     */
    private final TermRepository termRepository;

    /**
     * Final exam repository (create, specific stuff).
     */
    private final ExamRepository examRepository;

    /**
     * Midterm exam repository (same)
     */
    private final MidtermExamRepository midtermExamRepository;

    /**
     * Lecture repository (same)
     */
    private final LectureRepository lectureRepository;

    /**
     * Lab repository (same)
     */
    private final LabRepository labRepository;

    /**
     * To fetch teachers.
     */
    private final TeacherRepository teacherRepository;

    /**
     * To fetch rooms.
     */
    private final RoomRepository roomRepository;

    /**
     * Schedule service to update schedules of everyone affiliated with a term.
     */
    private final ScheduleService scheduleService;

    /**
     * Constructor for TermService.
     *
     * @param termRepository         the term repository
     * @param scheduleItemRepository the schedule item repository
     * @param examRepository         the final exam repository
     * @param midtermExamRepository  the midterm exam repository
     * @param scheduleService        the schedule service
     * @param teacherRepository      the teacher repository
     * @param roomRepository         the room repository
     */
    public TermService(TermRepository termRepository, ExamRepository examRepository,
            MidtermExamRepository midtermExamRepository, ScheduleService scheduleService,
            TeacherRepository teacherRepository, RoomRepository roomRepository, LabRepository labRepository,
            LectureRepository lectureRepository) {
        this.termRepository = termRepository;
        this.examRepository = examRepository;
        this.midtermExamRepository = midtermExamRepository;
        this.scheduleService = scheduleService;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.labRepository = labRepository;
        this.lectureRepository = lectureRepository;
    }

    /**
     * Creates a midterm exam based on the provided DTO.
     * 
     * @param dto  the term creation DTO
     * @param type the type of the term
     * @return the created lightweight term DTO
     */
    public LightweightTermDTO CreateNonExamTerm(TermCreationDTO dto, TermType type) {
        // Get needed entities
        Teacher supervisor = GetSupervisor(dto.getSupervisorUsername());
        Room room = GetRoom(dto.getRoomShortcut());

        Term term = CreateNonExamTermFromDTO(dto, type, supervisor, room);

        scheduleService.CreateScheduleForTerm(term, type.name());
        RegisterTerm(term, type, Optional.empty());
        
        // Save based on type
        if (type == TermType.MIDTERM_EXAM) {
            midtermExamRepository.save((MidtermExam) term);
        } else if (type == TermType.LAB) {
            labRepository.save((Lab) term);
        } else if (type == TermType.LECTURE) {
            lectureRepository.save((Lecture) term);
        }

        return ConvertToLightweightDTO(term);
    }

    /**
     * Bloated convenience method to create non-exam terms (lectures, labs,
     * midterms).
     * 
     * @param dto  the term creation DTO
     * @param type the type of the term
     * @param supervisor the supervisor teacher
     * @param room the room
     * @return the created term
     */
    private Term CreateNonExamTermFromDTO(TermCreationDTO dto, TermType type, Teacher supervisor, Room room) {
        if (type == TermType.MIDTERM_EXAM) {
            return MidtermExam.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getDate())
                    .duration(dto.getDuration())
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .supervisor(supervisor)
                    .room(room)
                    .build();
        } else if (type == TermType.LAB) {
            return Lab.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getDate())
                    .duration(dto.getDuration())
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .supervisor(supervisor)
                    .room(room)
                    .build();
        } else if (type == TermType.LECTURE) {
            return Lecture.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getDate())
                    .duration(dto.getDuration())
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .supervisor(supervisor)
                    .room(room)
                    .build();
        } else {
            throw new IllegalArgumentException("Unsupported term type for non-exam term creation: " + type.name());
        }
    }

    public LightweightTermDTO CreateFinalExam(ExamCreationDTO dto) {
        // Again, needed entities
        Teacher supervisor = GetSupervisor(dto.getSupervisorUsername());
        Room room = GetRoom(dto.getRoomShortcut());

        // Create final exam
        Exam exam = Exam.builder()
                .minPoints(dto.getMinPoints())
                .maxPoints(dto.getMaxPoints())
                .date(dto.getDate())
                .duration(dto.getDuration())
                .description(dto.getDescription())
                .name(dto.getName())
                .supervisor(supervisor)
                .room(room)
                .attempt(dto.getNofAttempt())
                .build();

        RegisterTerm(exam, TermType.EXAM, Optional.of(dto.getNofAttempt()));
        scheduleService.CreateScheduleForTerm(exam, TermType.EXAM.name());
        examRepository.save(exam);

        return ConvertToLightweightDTO(exam);
    }

    /**
     * Registers the given term to:
     * 1. All students who have this course (midterm, final without unit-credit)
     * 2. All students who have this course and have unit-credit (final with
     * unit-credit)
     * 3. All students who can go to the exam and failed the prior attempt.
     * 
     * @param term the term to register
     */
    private void RegisterTerm(Term term, TermType type, Optional<Integer> whichAttempt) {
        if (type == TermType.MIDTERM_EXAM) {
            RegisterForAll(term);
        }

        // Check based on the course end type and register accordingly
        Integer attempt = whichAttempt
                .orElseThrow(() -> new IllegalArgumentException("Attempt number is required for final exams."));

        Course course = term.getCourse();
        CourseEndType endType = course.getCompletedBy();
        Set<StudentCourse> studentCourses = course.getStudentCourses();
        Set<StudentTerm> students = new HashSet<StudentTerm>();

        // Register each student who is eligible to take the exam
        for (StudentCourse sc : studentCourses) {
            if (CanRegisterForFinalExam(sc, endType, attempt)) {
                StudentTerm studentTerm = StudentTerm.builder()
                        .student(sc.getStudent())
                        .term(term)
                        .build();
                students.add(studentTerm);
            }
        }

        term.setStudents(students);
    }

    /**
     * Registers the given midterm to all students who have this course.
     * 
     * @param term the midterm to register
     */
    private void RegisterForAll(Term midterm) {
        Course course = midterm.getCourse();
        Set<StudentCourse> studentCourses = course.getStudentCourses();
        Set<StudentTerm> students = midterm.getStudents();
        for (StudentCourse sc : studentCourses) {
            StudentTerm studentTerm = StudentTerm.builder()
                    .student(sc.getStudent())
                    .term(midterm)
                    .build();
            students.add(studentTerm);
        }

        midterm.setStudents(students);
    }

    /**
     * Checks if the student can register for the final exam.
     * 
     * @param student the student
     * @param endType the course end type
     * @return true if can register, false otherwise
     */
    private boolean CanRegisterForFinalExam(StudentCourse student, CourseEndType endType, Integer whichAttempt) {
        // Exam only
        if (endType == CourseEndType.EXAM) {
            return !student.getCompleted();
        }

        // Exam with unit-credit
        else if (endType == CourseEndType.UNIT_CREDIT_EXAM) {
            return student.getUnitCredit() && !student.getCompleted();
        }

        // Shouldn't happen
        throw new IllegalArgumentException("Invalid course end type for final exam registration: " + endType.name());
    }

    /**
     * Basically calls GetTeacherByID or throws.
     * 
     * @param supervisorUsername the username of the supervisor
     * @return the teacher
     */
    private Teacher GetSupervisor(String supervisorUsername) {
        return teacherRepository.findByUsername(supervisorUsername)
                .orElseThrow(() -> new NotFoundException("Teacher not found with username: " + supervisorUsername));
    }

    /**
     * Fetches the term room based on the provided shortcut.
     * 
     * @param roomShortcut the room shortcut
     * @return the fetched room
     */
    private Room GetRoom(String roomShortcut) {
        return roomRepository.findByShortcut(roomShortcut)
                .orElseThrow(() -> new NotFoundException("Room not found with shortcut: " + roomShortcut));
    }

    /**
     * Converts a Term entity to a LightweightTermDTO.
     * 
     * @param term the term entity
     * @return the lightweight term DTO
     */
    private LightweightTermDTO ConvertToLightweightDTO(Term term) {
        return new LightweightTermDTO(
                term.getName(), term.getDate(), term.getDuration(), term.getRoom().getShortcut());
    }
}