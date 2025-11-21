package IIS.wis2_backend.Services;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.Enum.TermType;
import IIS.wis2_backend.Enum.*;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Room.LabRoom;
import IIS.wis2_backend.Models.Room.LectureRoom;
import IIS.wis2_backend.Models.Room.Office;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Education.Schedule.ScheduleRepository;
import IIS.wis2_backend.Repositories.Room.OfficeRepository;
import IIS.wis2_backend.Repositories.Room.RoomRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Services.Education.TermService;

/**
 * Mock database service for testing purposes.
 */
@Service
public class MockDBService {
	/**
	 * User repository to clear users/check for existence.
	 */
	private final UserRepository userRepository;

	/**
	 * Course repository to create test courses.
	 */
	private final CourseRepository courseRepository;

	/**
	 * Office repository to give teachers.
	 */
	private final OfficeRepository officeRepository;

	/**
	 * TermService to create test terms.
	 */
	private final TermService termService;

	/**
	 * Room repository so we can create rooms for terms in the mock DB.
	 */
	private final RoomRepository roomRepository;

	/**
	 * Schedule repository.
	 */
	private final ScheduleRepository scheduleRepository;

	/**
	 * Password encoder to encode passwords.
	 */
	private final PasswordEncoder passwordEncoder;

	/**
	 * Constructor for MockDBService.
	 * 
	 * @param userRepository     User repository.
	 * @param courseRepository   Course repository.
	 * @param officeRepository   Office repository.
	 * @param termService        Term service.
	 * @param roomRepository     Room repository.
	 * @param scheduleRepository Schedule repository.
	 * @param passwordEncoder    Password encoder.
	 */
	public MockDBService(UserRepository userRepository,
			CourseRepository courseRepository, OfficeRepository officeRepository, TermService termService,
			RoomRepository roomRepository, ScheduleRepository scheduleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.officeRepository = officeRepository;
		this.termService = termService;
		this.roomRepository = roomRepository;
		this.scheduleRepository = scheduleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Inserts a teacher into the mock db if he doesn't exist yet.
	 * 
	 * @param firstName First name of the teacher.
	 * @param lastName  Last name of the teacher.
	 * @param email     Email of the teacher.
	 */
	private void InsertMockTeacherIfNotExists(String firstName, String lastName, String email, String office) {
		Office teacherOffice = officeRepository.findByShortcut(office);

		if (!userRepository.existsByEmail(email)) {
			Wis2User teacher = Wis2User.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("1980-01-01"))
					.email(email)
					.password(passwordEncoder.encode("pwd"))
					.activated(true)
					.role(IIS.wis2_backend.Enum.Roles.USER)
					.office(teacherOffice)
					.build();

			if (teacher != null) {
				Schedule schedule = Schedule.builder()
						.user(teacher)
						.items(new HashSet<>())
						.build();
				scheduleRepository.save(schedule);
				userRepository.save(teacher);
			}
		}
	}

	/**
	 * Inserts a student into the mock db if he doesn't exist yet.
	 * 
	 * @param firstName First name of the student.
	 * @param lastName  Last name of the student.
	 * @param email     Email of the student.
	 */
	private void InsertMockStudentIfNotExists(String firstName, String lastName, String email) {
		if (!userRepository.existsByEmail(email)) {
			Wis2User student = Wis2User.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("2000-01-01"))
					.password(passwordEncoder.encode("pwd"))
					.email(email)
					.activated(true)
					.role(Roles.USER)
					.build();

			if (student != null) {
				Schedule schedule = Schedule.builder()
						.user(student)
						.items(new HashSet<>())
						.build();
				userRepository.save(student);
			}
		}
	}

	/**
	 * Inserts a course into the mock db if it doesn't exist yet.
	 * 
	 * @param name     Name of the course.
	 * @param price    Price of the course.
	 * @param shortcut Shortcut of the course.
	 */
	private void InsertMockCourseIfNotExists(String name, Double price, String shortcut,
			IIS.wis2_backend.Enum.CourseEndType endType,
			Wis2User supervisor, Set<Wis2User> teachers) {
		if (!courseRepository.existsByShortcut(shortcut)) {
			Course course = Course.builder()
					.name(name)
					.price(price)
					.shortcut(shortcut)
					.completedBy(endType)
					.supervisor(supervisor)
					.teachers(teachers)
					.capacity(100)
					.autoregister(true)
					.build();

			// Create an empty schedule for the course
			Schedule schedule = Schedule.builder()
					.course(course)
					.items(new HashSet<>())
					.build();

			if (schedule == null) {
				return;
			}

			scheduleRepository.save(schedule);

			course.setSchedule(schedule);
			courseRepository.save(course);

		}
	}

	/**
	 * Inserts mock terms for ISA.
	 */
	public void InsertMockTermsForISA() {
		// Ensure some rooms exist for terms
		InsertRoomIfNotExists("LAB_A", "Main", "1", true, true);
		InsertRoomIfNotExists("LAB_B", "Main", "2", true, true);
		InsertRoomIfNotExists("LEC_1", "Main", "1", false, null);
		InsertRoomIfNotExists("LEC_2", "Annex", "1", false, null);

		// find the ISA course
		Course isa = courseRepository.findAll().stream()
				.filter(c -> "ISA".equals(c.getShortcut()))
				.findFirst()
				.orElse(null);

		if (isa == null) {
			return;
		}

		String courseShortcut = isa.getShortcut();
		String supervisorUsername = isa.getSupervisor() != null ? isa.getSupervisor().getUsername() : null;
		if (supervisorUsername == null) {
			// nothing we can do without a supervisor
			return;
		}

		java.time.LocalDateTime base = java.time.LocalDateTime.now().plusDays(7);

		// Create ~20 terms across several weeks and types
		for (int i = 0; i < 20; i++) {
			java.time.LocalDateTime date = base.plusWeeks(i / 3).withHour(9 + (i % 5)).withMinute(0).withSecond(0)
					.withNano(0);
			System.out.println("Creating mock term at " + date.toString());

			// Alternate types: every 6th is final exam, others are midterms, labs or
			// lectures
			if (i % 6 == 5) {
				// final exam
				IIS.wis2_backend.DTO.Request.Term.ExamCreationDTO exam = IIS.wis2_backend.DTO.Request.Term.ExamCreationDTO
						.builder()
						.name("ISA Final Exam " + (i / 6 + 1))
						.minPoints(0)
						.maxPoints(100)
						.date(date)
						.duration(120)
						.description("Final exam for ISA - mock entry " + i)
						.autoRegistration(true)
						.courseShortcut(courseShortcut)
						.supervisorUsername(supervisorUsername)
						.roomShortcut(i % 2 == 0 ? "LAB_A" : "LEC_1")
						.type(TermType.EXAM)
						.nofAttempt(1)
						.build();

				try {
					termService.CreateFinalExam(exam);
				} catch (Exception ex) {
					// ignore failures in mock seeding
				}
			} else {
				// non-exam term (midterm, lab, lecture)
				TermCreationDTO dto = TermCreationDTO.builder()
						.name((i % 3 == 0 ? "ISA Midterm " : (i % 3 == 1 ? "ISA Lab " : "ISA Lecture ")) + (i + 1))
						.minPoints(0)
						.maxPoints(100)
						.date(date)
						.duration(i % 3 == 0 ? 90 : 60)
						.description("Mock term " + i)
						.autoRegistration(true)
						.courseShortcut(courseShortcut)
						.supervisorUsername(supervisorUsername)
						.roomShortcut(i % 3 == 1 ? "LAB_B" : "LEC_2")
						.type(i % 3 == 0 ? TermType.MIDTERM_EXAM : (i % 3 == 1 ? TermType.LAB : TermType.LECTURE))
						.build();

				try {
					if (i % 3 == 0) {
						// midterm
						termService.CreateNonExamTerm(dto);
					} else if (i % 3 == 1) {
						// lab
						termService.CreateNonExamTerm(dto);
					} else {
						// lecture
						termService.CreateNonExamTerm(dto);
					}
				} catch (Exception ex) {
					// swallow exceptions during mock seeding
				}
			}
		}
	}

	/**
	 * Inserts an office into the mock db if it doesn't exist yet.
	 * 
	 * @param shortcut Shortcut of the office.
	 */
	private void InsertOfficeIfNotExists(String shortcut) {
		if (!officeRepository.existsByShortcut(shortcut)) {
			Office office = Office.builder()
					.shortcut(shortcut)
					.building("Offices")
					.floor("1")
					.capacity(5)
					.build();

			if (office == null) {
				return;
			}
			officeRepository.save(office);
		}
	}

	/**
	 * Inserts a room into the mock db if it doesn't exist yet.
	 * Supports creating simple LectureRoom or LabRoom.
	 *
	 * @param shortcut  room shortcut/tag
	 * @param building  building name
	 * @param floor     floor description
	 * @param isLab     if true create LabRoom, otherwise LectureRoom
	 * @param pcSupport only relevant for lab rooms (may be null)
	 */
	private void InsertRoomIfNotExists(String shortcut, String building, String floor, boolean isLab,
			Boolean pcSupport) {
		if (roomRepository.findByShortcut(shortcut).isPresent()) {
			return;
		}

		if (isLab) {
			LabRoom lab = LabRoom
					.builder()
					.shortcut(shortcut)
					.building(building)
					.floor(floor)
					.capacity(30)
					.pcSupport(pcSupport == null ? Boolean.FALSE : pcSupport)
					.build();
			roomRepository.save(lab);
		} else {
			LectureRoom lec = new LectureRoom();
			lec.setShortcut(shortcut);
			lec.setBuilding(building);
			lec.setFloor(floor);
			lec.setCapacity(100);
			roomRepository.save(lec);
		}
	}

	/**
	 * Runs all the InsertMockX methods.
	 */
	@Transactional
	@EventListener(ApplicationReadyEvent.class)
	public void InitializeMockDB() {
		InsertMockOffices();
		InsertMockStudents();
		InsertMockTeachers();
		InsertMockCourses();
		// create mock terms/schedule for ISA
		InsertMockTermsForISA();
		// create special teacher with courses and students
		InsertSpecialTeacher();
	}

	/**
	 * Insert offices needed for teachers.
	 */
	public void InsertMockOffices() {
		InsertOfficeIfNotExists("A1.12");
		InsertOfficeIfNotExists("B2.34");
		InsertOfficeIfNotExists("C3.56");
		InsertOfficeIfNotExists("D4.78");
		InsertOfficeIfNotExists("E5.90");
	}

	/**
	 * Initialize the mock database with test students.
	 */
	public void InsertMockStudents() {
		InsertMockStudentIfNotExists("Igor", "Lacko", "xlackoi00@fit.vutbr.cz");
		InsertMockStudentIfNotExists("Jakub", "Kapitulcin", "xkapitj00@fit.vutbr.cz");
		InsertMockStudentIfNotExists("Milan", "Babuljak", "xbabulm00@fit.vutbr.cz");
		InsertMockStudentIfNotExists("Jaroslav", "Synek", "xsynekj00@fit.vutbr.cz");
		InsertMockStudentIfNotExists("Adam", "Bisa", "xbisaad00@fit.vutbr.cz");
	}

	/**
	 * Initialize the mock database with test teachers.
	 */
	public void InsertMockTeachers() {
		InsertMockTeacherIfNotExists("Petr", "Pavel", "ppavel@fit.vutbr.cz", "A1.12");
		InsertMockTeacherIfNotExists("John", "Doe", "jdoe@fit.vutbr.cz", "B2.34");
		InsertMockTeacherIfNotExists("Alice", "Johnson", "alice.johnson@example.com", "A1.12");
		InsertMockTeacherIfNotExists("Martin", "Smith", "martin.smith@example.com", "C3.56");
		InsertMockTeacherIfNotExists("Eva", "Brown", "eva.brown@example.com", "D4.78");
		InsertMockTeacherIfNotExists("Thomas", "Cooper", "thomas.cooper@example.com", "E5.90");
		InsertMockTeacherIfNotExists("Clara", "Taylor", "clara.taylor@example.com", "A1.12");
		InsertMockTeacherIfNotExists("Lucas", "White", "lucas.white@example.com", "B2.34");
		InsertMockTeacherIfNotExists("Paul", "Davis", "paul.davis@example.com", "C3.56");
		InsertMockTeacherIfNotExists("Monica", "Green", "monica.green@example.com", "D4.78");
		InsertMockTeacherIfNotExists("Robert", "Baker", "robert.baker@example.com", "E5.90");
		InsertMockTeacherIfNotExists("Veronica", "Hall", "veronica.hall@example.com", "A1.12");
	}

	/**
	 * Initialize the mock database with test courses.
	 */
	public void InsertMockCourses() {
		// find teachers by email (they are created in InsertMockTeachers)
		Wis2User pavel = userRepository.findAll().stream()
				.filter(t -> "ppavel@fit.vutbr.cz".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User jdoe = userRepository.findAll().stream()
				.filter(t -> "jdoe@fit.vutbr.cz".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User alice = userRepository.findAll().stream()
				.filter(t -> "alice.johnson@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User martin = userRepository.findAll().stream()
				.filter(t -> "martin.smith@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User eva = userRepository.findAll().stream()
				.filter(t -> "eva.brown@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User thomas = userRepository.findAll().stream()
				.filter(t -> "thomas.cooper@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User clara = userRepository.findAll().stream()
				.filter(t -> "clara.taylor@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User lucas = userRepository.findAll().stream()
				.filter(t -> "lucas.white@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User paul = userRepository.findAll().stream()
				.filter(t -> "paul.davis@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User monica = userRepository.findAll().stream()
				.filter(t -> "monica.green@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User robert = userRepository.findAll().stream()
				.filter(t -> "robert.baker@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Wis2User veronica = userRepository.findAll().stream()
				.filter(t -> "veronica.hall@example.com".equals(t.getEmail())).findFirst().orElse(null);

		// build teacher sets for courses
		java.util.Set<Wis2User> iisTeachers = new java.util.HashSet<>();
		if (pavel != null)
			iisTeachers.add(pavel);
		if (alice != null)
			iisTeachers.add(alice);

		java.util.Set<Wis2User> idsTeachers = new java.util.HashSet<>();
		if (jdoe != null)
			idsTeachers.add(jdoe);
		if (martin != null)
			idsTeachers.add(martin);

		java.util.Set<Wis2User> iusTeachers = new java.util.HashSet<>();
		if (alice != null)
			iusTeachers.add(alice);
		if (eva != null)
			iusTeachers.add(eva);

		java.util.Set<Wis2User> ipkTeachers = new java.util.HashSet<>();
		if (martin != null)
			ipkTeachers.add(martin);
		if (thomas != null)
			ipkTeachers.add(thomas);

		java.util.Set<Wis2User> isaTeachers = new java.util.HashSet<>();
		if (eva != null)
			isaTeachers.add(eva);
		if (clara != null)
			isaTeachers.add(clara);

		java.util.Set<Wis2User> iosTeachers = new java.util.HashSet<>();
		if (thomas != null)
			iosTeachers.add(thomas);
		if (lucas != null)
			iosTeachers.add(lucas);

		java.util.Set<Wis2User> ifjTeachers = new java.util.HashSet<>();
		if (clara != null)
			ifjTeachers.add(clara);
		if (paul != null)
			ifjTeachers.add(paul);
		if (veronica != null)
			ifjTeachers.add(veronica);

		java.util.Set<Wis2User> izuTeachers = new java.util.HashSet<>();
		if (lucas != null)
			izuTeachers.add(lucas);
		if (monica != null)
			izuTeachers.add(monica);
		if (robert != null)
			izuTeachers.add(robert);

		// create courses with supervisors and teacher sets
		// ensure courses exist so we can add descriptions (calls are idempotent)
		InsertMockCourseIfNotExists("Information Systems", 1500.0, "IIS", CourseEndType.EXAM, pavel, iisTeachers);
		InsertMockCourseIfNotExists("Database Systems", 1200.0, "IDS", CourseEndType.UNIT_CREDIT, jdoe, idsTeachers);
		InsertMockCourseIfNotExists("Introduction to Software Engineering", 1300.0, "IUS",
				CourseEndType.GRADED_UNIT_CREDIT, alice, iusTeachers);
		InsertMockCourseIfNotExists("Computer Communications and Networks", 1400.0, "IPK", CourseEndType.EXAM, martin,
				ipkTeachers);
		InsertMockCourseIfNotExists("Network Applications and Network Administration", 1600.0, "ISA",
				CourseEndType.UNIT_CREDIT_EXAM, eva, isaTeachers);
		InsertMockCourseIfNotExists("Operating Systems", 10000.0, "IOS", CourseEndType.GRADED_UNIT_CREDIT, thomas,
				iosTeachers);
		InsertMockCourseIfNotExists("Formal Languages and Compilers", 1100.0, "IFJ", CourseEndType.EXAM, clara,
				ifjTeachers);
		InsertMockCourseIfNotExists("Introduction to Artificial Intelligence", 1700.0, "IZU", CourseEndType.UNIT_CREDIT,
				lucas, izuTeachers);

		// descriptions (~5 lines each)
		java.util.Map<String, String> descriptions = new java.util.HashMap<>();
		descriptions.put("IIS",
				"Information Systems explores the role of information in organizations and society.\n"
						+ "Students learn system design principles, data modeling and process analysis.\n"
						+ "Emphasis is placed on aligning IT solutions with business needs.\n"
						+ "Case studies and practical examples illustrate real-world challenges.\n"
						+ "By the end, students can propose and evaluate IS solutions.");
		descriptions.put("IDS",
				"Database Systems provides foundations of relational databases and SQL.\n"
						+ "Topics include schema design, normalization and indexing strategies.\n"
						+ "Students will implement queries and study transaction management.\n"
						+ "Performance and scalability considerations are also discussed.\n"
						+ "Labs reinforce both theory and practical database usage.");
		descriptions.put("IUS",
				"Introduction to Software Engineering covers software development lifecycles.\n"
						+ "It introduces requirements engineering, design patterns and testing.\n"
						+ "Students work on team projects to practice agile methodologies.\n"
						+ "Quality assurance and maintainability are emphasised throughout.\n"
						+ "The course prepares students for collaborative software projects.");
		descriptions.put("IPK",
				"Computer Communications and Networks introduces network architectures and protocols.\n"
						+ "Key concepts include routing, transport protocols and network layers.\n"
						+ "Students study performance, security and network management.\n"
						+ "Hands-on exercises demonstrate packet behavior and troubleshooting.\n"
						+ "The course builds a strong foundation for networking topics.");
		descriptions.put("ISA",
				"Network Applications and Administration focuses on deploying networked services.\n"
						+ "Students learn server configuration, DNS, and network services management.\n"
						+ "Security, access control and monitoring are core topics.\n"
						+ "Practical labs cover real-world administration tasks and tooling.\n"
						+ "This course readies students for operational network roles.");
		descriptions.put("IOS",
				"Operating Systems covers processes, memory and file system concepts.\n"
						+ "Synchronization, scheduling and resource management are studied in depth.\n"
						+ "Students implement parts of an OS to understand internals.\n"
						+ "Performance, concurrency and security considerations are highlighted.\n"
						+ "The course provides hands-on exposure to OS design decisions.");
		descriptions.put("IFJ",
				"Formal Languages and Compilers explores grammars, parsing and language theory.\n"
						+ "Students study lexical analysis, syntax trees and semantic analysis.\n"
						+ "Compiler construction techniques and optimization are introduced.\n"
						+ "Practical assignments involve building simple language processors.\n"
						+ "The course bridges theory and implementation of programming languages.");
		descriptions.put("IZU",
				"Introduction to Artificial Intelligence surveys core AI techniques and models.\n"
						+ "Topics include search, knowledge representation and machine learning basics.\n"
						+ "Students apply algorithms to problem solving and data-driven tasks.\n"
						+ "Ethical considerations and real-world applications are discussed.\n"
						+ "Hands-on projects illustrate the strengths and limits of AI methods.");

		// apply descriptions to courses
		for (java.util.Map.Entry<String, String> e : descriptions.entrySet()) {
			final String shortcut = e.getKey();
			final String desc = e.getValue();
			courseRepository.findAll().stream()
					.filter(c -> shortcut.equals(c.getShortcut()))
					.findFirst()
					.ifPresent(c -> {
						c.setDescription(desc);
						courseRepository.save(c);
					});
		}
	}

	/**
	 * Inserts a special teacher with courses, deadlines and students.
	 */
	public void InsertSpecialTeacher() {
		// Create Teacher
		String teacherEmail = "special.teacher@example.com";
		if (!userRepository.existsByEmail(teacherEmail)) {
			Wis2User teacher = Wis2User.builder()
					.firstName("Special")
					.lastName("Teacher")
					.username("special.teacher")
					.email(teacherEmail)
					.password(passwordEncoder.encode("password"))
					.birthday(Date.valueOf("1985-05-05"))
					.activated(true)
					.role(Roles.TEACHER)
					.build();
			userRepository.save(teacher);
		}

		Wis2User teacher = userRepository.findByEmail(teacherEmail).orElse(null);
		if (teacher == null)
			return;

		// Create Course
		String courseShortcut = "SPC";
		if (!courseRepository.existsByShortcut(courseShortcut)) {
			Set<Wis2User> teachers = new HashSet<>();
			teachers.add(teacher);

			Course course = Course.builder()
					.name("Special Course")
					.price(500.0)
					.shortcut(courseShortcut)
					.completedBy(CourseEndType.EXAM)
					.supervisor(teacher)
					.teachers(teachers)
					.capacity(50)
					.autoregister(true)
					.description("A special course for testing.")
					.build();

			Schedule schedule = Schedule.builder()
					.course(course)
					.items(new HashSet<>())
					.build();
			scheduleRepository.save(schedule);
			course.setSchedule(schedule);
			courseRepository.save(course);
		}

		// Create Students
		Course course = courseRepository.findByShortcut(courseShortcut).orElse(null);
		for (int i = 1; i <= 5; i++) {
			String studentEmail = "special.student" + i + "@example.com";
			if (!userRepository.existsByEmail(studentEmail)) {
				Wis2User student = Wis2User.builder()
						.firstName("Special")
						.lastName("Student" + i)
						.username("special.student" + i)
						.email(studentEmail)
						.password(passwordEncoder.encode("password"))
						.birthday(Date.valueOf("2005-01-01"))
						.activated(true)
						.role(Roles.USER)
						.build();
				userRepository.save(student);
			}

			if (course != null) {
				Wis2User student = userRepository.findByEmail(studentEmail).orElse(null);
				if (student != null) {
					boolean enrolled = course.getStudentCourses().stream()
							.anyMatch(sc -> sc.getStudent().getId().equals(student.getId()));
					if (!enrolled) {
						StudentCourse sc = StudentCourse.builder()
								.student(student)
								.course(course)
								.points(0)
								.completed(false)
								.failed(false)
								.build();
						course.getStudentCourses().add(sc);
					}
				}
			}
		}
		if (course != null) {
			courseRepository.save(course);
		}

		// Create Terms (Deadlines)
		java.time.LocalDateTime base = java.time.LocalDateTime.now().plusDays(3);
		for (int i = 0; i < 5; i++) {
			java.time.LocalDateTime date = base.plusWeeks(i);
			TermCreationDTO dto = TermCreationDTO.builder()
					.name("Special Assignment " + (i + 1))
					.minPoints(0)
					.maxPoints(20)
					.date(date)
					.duration(60)
					.description("Special assignment deadline " + i)
					.autoRegistration(true)
					.courseShortcut(courseShortcut)
					.supervisorUsername(teacher.getUsername())
					.roomShortcut("LAB_A") // Assuming LAB_A exists from InsertMockTermsForISA
					.type(TermType.LAB)
					.build();
			try {
				termService.CreateNonExamTerm(dto);
			} catch (Exception ex) {
				// ignore
			}
		}
	}

	/**
	 * Clear the mock database.
	 */
	public void ClearMockDB() {
		userRepository.deleteAll();
		courseRepository.deleteAll();
	}
}