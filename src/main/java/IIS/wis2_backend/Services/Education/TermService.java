package IIS.wis2_backend.Services.Education;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.DTO.Response.Term.LightweightTermDTO;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.TermType;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UnauthorizedException;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.Room.StudyRoom;
import IIS.wis2_backend.Models.Term.*;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.Room.StudyRoomRepository;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Education.Term.ExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.LabRepository;
import IIS.wis2_backend.Repositories.Education.Term.LectureRepository;
import IIS.wis2_backend.Repositories.Education.Term.MidtermExamRepository;
import IIS.wis2_backend.Repositories.Education.Term.TermRepository;
import IIS.wis2_backend.Repositories.Relational.StudentTermRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * Service for managing terms.
 */
@Service
@Transactional
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
    private final UserRepository userRepository;

    /**
     * To fetch rooms.
     */
    private final StudyRoomRepository studyRoomRepository;

    /**
     * To fetch courses.
     */
    private final CourseRepository courseRepository;

    /**
     * Schedule service to update schedules of everyone affiliated with a term.
     */
    private final ScheduleService scheduleService;

    /**
     * Student term repository to manage student-term relationships.
     */
    private final StudentTermRepository studentTermRepository;

    /**
     * Constructor for TermService.
     *
     * @param termRepository         the term repository
     * @param scheduleItemRepository the schedule item repository
     * @param examRepository         the final exam repository
     * @param midtermExamRepository  the midterm exam repository
     * @param scheduleService        the schedule service
     * @param userRepository         the user repository
     * @param roomRepository         the room repository
     * @param labRepository          the lab repository
     * @param lectureRepository      the lecture repository
     * @param courseRepository       the course repository
     * @param studentTermRepository  the student term repository
     */
    public TermService(TermRepository termRepository, ExamRepository examRepository,
            MidtermExamRepository midtermExamRepository, ScheduleService scheduleService,
            UserRepository userRepository, StudyRoomRepository studyRoomRepository, LabRepository labRepository,
            LectureRepository lectureRepository, CourseRepository courseRepository,
            StudentTermRepository studentTermRepository) {
        this.termRepository = termRepository;
        this.examRepository = examRepository;
        this.midtermExamRepository = midtermExamRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.labRepository = labRepository;
        this.lectureRepository = lectureRepository;
        this.courseRepository = courseRepository;
        this.studentTermRepository = studentTermRepository;
    }

    /**
     * Creates a midterm exam based on the provided DTO.
     * 
     * @param dto the term creation DTO
     * @return the created lightweight term DTO
     */
    public LightweightTermDTO CreateNonExamTerm(String shortcut, TermCreationDTO dto, String supervisorUsername) {
        Course course = courseRepository.findByShortcut(shortcut)
                .orElseThrow(() -> new NotFoundException("Course not found with shortcut: " + shortcut));

        if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
            throw new UnauthorizedException("Supervisor username does not match course supervisor.");
        }

        // Get needed entities
        StudyRoom room = GetRoom(dto.getRoomShortcut());

        // Check if the room is free
        if (!studyRoomRepository.isAvaliableBetween(dto.getRoomShortcut(), dto.getStartDate(),
                dto.getStartDate().plusMinutes(dto.getDuration()))) {
            throw new UnauthorizedException("Room is not available in the given time interval.");
        }

        Term term = CreateNonExamTermFromDTO(dto, room, course);
        TermType type = dto.getType();

        // Save based on type to get ID
        if (type == TermType.MIDTERM_EXAM) {
            term = midtermExamRepository.save((MidtermExam) term);
        } else if (type == TermType.LAB) {
            term = labRepository.save((Lab) term);
        } else if (type == TermType.LECTURE) {
            term = lectureRepository.save((Lecture) term);
        }

        if (dto.getAutoregister()) {
            RegisterTerm(term);
            // Save again to persist students
            if (type == TermType.MIDTERM_EXAM) {
                term = midtermExamRepository.save((MidtermExam) term);
            } else if (type == TermType.LAB) {
                term = labRepository.save((Lab) term);
            } else if (type == TermType.LECTURE) {
                term = lectureRepository.save((Lecture) term);
            }
        }

        scheduleService.CreateScheduleForTerm(term, type);

        return ConvertToLightweightDTO(term, type);
    }

    /**
     * Bloated convenience method to create non-exam terms (lectures, labs,
     * midterms).
     * 
     * @param dto        the term creation DTO
     * @param supervisor the supervisor teacher
     * @param room       the room
     * @return the created term
     */
    private Term CreateNonExamTermFromDTO(TermCreationDTO dto, StudyRoom room, Course course) {
        TermType type = dto.getType();
        if (type == TermType.MIDTERM_EXAM) {
            return MidtermExam.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getStartDate())
                    .duration(dto.getDuration())
                    .endDate(dto.getStartDate().plusMinutes(dto.getDuration()))
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .room(room)
                    .course(course)
                    .termType(type)
                    .build();
        } else if (type == TermType.LAB) {
            return Lab.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getStartDate())
                    .duration(dto.getDuration())
                    .endDate(dto.getStartDate().plusMinutes(dto.getDuration()))
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .room(room)
                    .course(course)
                    .termType(type)
                    .build();
        } else if (type == TermType.LECTURE) {
            return Lecture.builder()
                    .minPoints(dto.getMinPoints())
                    .maxPoints(dto.getMaxPoints())
                    .date(dto.getStartDate())
                    .duration(dto.getDuration())
                    .endDate(dto.getStartDate().plusMinutes(dto.getDuration()))
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .room(room)
                    .course(course)
                    .termType(type)
                    .build();
        } else {
            throw new IllegalArgumentException("Unsupported term type for non-exam term creation: " + type.name());
        }
    }

    public LightweightTermDTO CreateFinalExam(String shortcut, TermCreationDTO dto, String supervisorUsername) {
        Course course = courseRepository.findByShortcut(shortcut)
                .orElseThrow(() -> new NotFoundException("Course not found with shortcut: " + shortcut));

        if (!course.getSupervisor().getUsername().equals(supervisorUsername)) {
            throw new UnauthorizedException("Supervisor username does not match course supervisor.");
        }

        // Again, needed entities
        StudyRoom room = GetRoom(dto.getRoomShortcut());

        // Create final exam
        Exam exam = Exam.builder()
                .minPoints(dto.getMinPoints())
                .maxPoints(dto.getMaxPoints())
                .date(dto.getStartDate())
                .duration(dto.getDuration())
                .endDate(dto.getStartDate().plusMinutes(dto.getDuration()))
                .description(dto.getDescription())
                .name(dto.getName())
                .room(room)
                .course(course)
                .termType(TermType.EXAM)
                .build();

        exam = examRepository.save(exam);

        if (dto.getAutoregister()) {
            RegisterTerm(exam);
            exam = examRepository.save(exam);
        }
        scheduleService.CreateScheduleForTerm(exam, TermType.EXAM);

        return ConvertToLightweightDTO(exam, TermType.EXAM);
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
    private void RegisterTerm(Term term) {
        if (term.getTermType() == TermType.MIDTERM_EXAM) {
            RegisterForAll(term);
        }

        Course course = term.getCourse();
        CourseEndType endType = course.getCompletedBy();
        Set<StudentCourse> studentCourses = course.getStudentCourses();
        Set<StudentTerm> students = new HashSet<StudentTerm>();

        // Register each student who is eligible to take the exam
        for (StudentCourse sc : studentCourses) {
            if (sc.getStatus() == RequestStatus.APPROVED && CanRegisterForFinalExam(sc, endType)) {
                StudentTerm studentTerm = StudentTerm.builder()
                        .student(sc.getStudent())
                        .term(term)
                        .build();
                students.add(studentTerm);
            }
        }

        term.setStudentTerms(students);
    }

    /**
     * Registers the given midterm to all students who have this course.
     * 
     * @param term the midterm to register
     */
    private void RegisterForAll(Term midterm) {
        Course course = midterm.getCourse();
        Set<StudentCourse> studentCourses = course.getStudentCourses();
        Set<StudentTerm> students = midterm.getStudentTerms();
        for (StudentCourse sc : studentCourses) {
            if (sc.getStatus() == RequestStatus.APPROVED) {
                StudentTerm studentTerm = StudentTerm.builder()
                        .student(sc.getStudent())
                        .term(midterm)
                        .build();
                students.add(studentTerm);
            }
        }

        midterm.setStudentTerms(students);
    }

    /**
     * Checks if the student can register for the final exam.
     * 
     * @param student the student
     * @param endType the course end type
     * @return true if can register, false otherwise
     */
    private boolean CanRegisterForFinalExam(StudentCourse student, CourseEndType endType) {
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
    private Wis2User GetSupervisor(String supervisorUsername) {
        return userRepository.findByUsername(supervisorUsername)
                .orElseThrow(() -> new NotFoundException("Teacher not found with username: " + supervisorUsername));
    }

    /**
     * Fetches the term room based on the provided shortcut.
     * 
     * @param roomShortcut the room shortcut
     * @return the fetched room
     */
    private StudyRoom GetRoom(String roomShortcut) {
        return studyRoomRepository.findMaybeByShortcut(roomShortcut)
                .orElseThrow(() -> new NotFoundException("Room not found with shortcut: " + roomShortcut));
    }

    /**
     * Converts a Term entity to a LightweightTermDTO.
     * 
     * @param term the term entity
     * @return the lightweight term DTO
     */
    private LightweightTermDTO ConvertToLightweightDTO(Term term, TermType type) {
        return new LightweightTermDTO(
                term.getId(), term.getName(), term.getDate(), term.getDuration(), term.getRoom().getShortcut(), type);
    }

    /**
	 * Registers a student to a specific term.
	 * 
	 * @param termId   The ID of the term.
	 * @param username The username of the student.
	 */
	@Transactional
	public void RegisterStudentToTerm(Long termId, String username) {
		Term term = termRepository.findById(termId)
				.orElseThrow(() -> new NotFoundException("Term not found with ID: " + termId));

		Wis2User student = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Student not found with username: " + username));

		Course course = term.getCourse();

		// Check if student is enrolled in the course
		StudentCourse studentCourse = course.getStudentCourses().stream()
				.filter(sc -> sc.getStudent().getId().equals(student.getId())
						&& sc.getStatus() == RequestStatus.APPROVED)
				.findFirst()
				.orElseThrow(() -> new UnauthorizedException("Student is not enrolled in the course!"));

		// Check if already registered
		if (term.getStudentTerms().stream()
				.anyMatch(st -> st.getStudent().getId().equals(student.getId()))) {
			throw new IllegalArgumentException("Student is already registered for this term!");
		}

		// Check capacity (room capacity)
		int currentRegistrations = term.getStudentTerms().size();
		if (currentRegistrations >= term.getRoom().getCapacity()) {
			throw new IllegalArgumentException("Term is full!");
		}

		// Register - create and persist the StudentTerm entity
		StudentTerm studentTerm = StudentTerm.builder()
				.student(student)
				.term(term)
				.build();

		studentTerm = studentTermRepository.save(studentTerm);

		// Update schedule
		scheduleService.AddTermToUserSchedule(term, student);
	}

	/**
	 * Unregisters a student from a specific term.
	 * 
	 * @param termId   The ID of the term.
	 * @param username The username of the student.
	 */
	@Transactional
	public void UnregisterStudentFromTerm(Long termId, String username) {
		Term term = termRepository.findById(termId)
				.orElseThrow(() -> new NotFoundException("Term not found with ID: " + termId));

		Wis2User student = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Student not found with username: " + username));

		// Verify student is registered
		studentTermRepository.findByTermIdAndStudentId(termId, student.getId())
				.orElseThrow(() -> new IllegalArgumentException("Student is not registered for this term!"));

		// Update schedule first (before deleting the entity)
		scheduleService.RemoveTermFromUserSchedule(term, student);

		// Delete the StudentTerm entity using custom query
		studentTermRepository.deleteByTermIdAndStudentId(termId, student.getId());
	}
}