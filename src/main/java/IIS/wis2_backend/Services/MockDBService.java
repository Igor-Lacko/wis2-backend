package IIS.wis2_backend.Services;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.DTO.Request.Term.TermCreationDTO;
import IIS.wis2_backend.Enum.*;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Room.StudyRoom;
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
			Schedule schedule = Schedule.builder()
					.items(new HashSet<>())
					.build();

			Wis2User teacher = Wis2User.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("1980-01-01"))
					.email(email)
					.password(passwordEncoder.encode("pwd"))
					.activated(true)
					.role(Roles.TEACHER)
					.office(teacherOffice)
					.schedule(schedule)
					.build();

			schedule.setUser(teacher);
			userRepository.save(teacher);
		} else {
			Wis2User teacher = userRepository.findByEmail(email).orElse(null);
			if (teacher != null) {
				teacher.setRole(Roles.TEACHER);
				teacher.setOffice(teacherOffice);
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
			Schedule schedule = Schedule.builder()
					.items(new HashSet<>())
					.build();

			Wis2User student = Wis2User.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("2000-01-01"))
					.password(passwordEncoder.encode("pwd"))
					.email(email)
					.activated(true)
					.role(Roles.USER)
					.schedule(schedule)
					.build();

			schedule.setUser(student);
			userRepository.save(student);
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
					.status(RequestStatus.APPROVED)
					.build();

			// Create an empty schedule for the course
			Schedule schedule = Schedule.builder()
					.course(course)
					.items(new HashSet<>())
					.build();

			if (schedule == null) {
				return;
			}

			course.setSchedule(schedule);
			courseRepository.save(course);
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
					.capacity(10)
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

		StudyRoom lec = new StudyRoom();
		lec.setShortcut(shortcut);
		lec.setBuilding(building);
		lec.setFloor(floor);
		lec.setCapacity(150);
		roomRepository.save(lec);
	}

	/**
	 * Runs all the InsertMockX methods.
	 */
	@Transactional
	@EventListener(ApplicationReadyEvent.class)
	public void InitializeMockDB() {
		InsertMockOffices();
		InsertMockStudyRooms();
		InsertMockStudents();
		InsertMockTeachers();
		InsertMockCourses();
		// create special teacher with courses and students
		InsertSpecialTeacher();
		InsertFrontendTestScenario();
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
	 * Insert study rooms needed for terms.
	 */
	public void InsertMockStudyRooms() {
		InsertRoomIfNotExists("LAB_A", "Main", "1", true, true);
		InsertRoomIfNotExists("LAB_B", "Main", "2", true, true);
		InsertRoomIfNotExists("LEC_1", "Main", "1", false, null);
		InsertRoomIfNotExists("LEC_2", "Annex", "1", false, null);
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
		InsertMockTeacherIfNotExists("Helena", "Novak", "helena.novak@example.com", "B2.34");
		InsertMockTeacherIfNotExists("Matej", "Urban", "matej.urban@example.com", "C3.56");
		InsertMockTeacherIfNotExists("Silvia", "Kral", "silvia.kral@example.com", "D4.78");
		InsertMockTeacherIfNotExists("Victor", "Stone", "victor.stone@example.com", "E5.90");
		InsertMockTeacherIfNotExists("Nina", "Sharp", "nina.sharp@example.com", "A1.12");
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

		// Add IZP (Introduction to Programming) - basic first-year course
		java.util.Set<Wis2User> izpTeachers = new java.util.HashSet<>();
		if (paul != null)
			izpTeachers.add(paul);
		if (monica != null)
			izpTeachers.add(monica);
		InsertMockCourseIfNotExists("Introduction to Programming Systems", 1000.0, "IZP", CourseEndType.UNIT_CREDIT_EXAM,
				paul, izpTeachers);

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
		descriptions.put("IZP",
				"Introduction to Programming Systems teaches basic programming concepts.\n"
						+ "Students learn C language fundamentals, data types and control structures.\n"
						+ "Memory management and pointers are introduced systematically.\n"
						+ "Weekly programming assignments build problem-solving skills.\n"
						+ "This foundational course prepares students for advanced programming.");

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
			Schedule schedule = Schedule.builder()
					.items(new HashSet<>())
					.build();

			Wis2User teacher = Wis2User.builder()
					.firstName("Special")
					.lastName("Teacher")
					.username("special.teacher")
					.email(teacherEmail)
					.password(passwordEncoder.encode("password"))
					.birthday(Date.valueOf("1985-05-05"))
					.activated(true)
					.role(Roles.TEACHER)
					.schedule(schedule)
					.build();

			schedule.setUser(teacher);
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
			course.setSchedule(schedule);
			courseRepository.save(course);
		}

		// Create Students
		Course course = courseRepository.findByShortcut(courseShortcut).orElse(null);
		for (int i = 1; i <= 5; i++) {
			String studentEmail = "special.student" + i + "@example.com";
			if (!userRepository.existsByEmail(studentEmail)) {
				Schedule schedule = Schedule.builder()
						.items(new HashSet<>())
						.build();

				Wis2User student = Wis2User.builder()
						.firstName("Special")
						.lastName("Student" + i)
						.username("special.student" + i)
						.email(studentEmail)
						.password(passwordEncoder.encode("password"))
						.birthday(Date.valueOf("2005-01-01"))
						.activated(true)
						.role(Roles.USER)
						.schedule(schedule)
						.build();

				schedule.setUser(student);
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
								.status(RequestStatus.APPROVED)
								.build();
						course.getStudentCourses().add(sc);
					}
				}
			}
		}
		if (course != null) {
			courseRepository.save(course);
		}
	}

	/**
	 * Inserts a scenario for frontend testing.
	 */
	public void InsertFrontendTestScenario() {
		// 1. Create Users
		String supervisorEmail = "frontend.supervisor@example.com";
		String teacherEmail = "frontend.teacher@example.com";
		String student1Email = "frontend.student1@example.com";
		String student2Email = "frontend.student2@example.com";

		InsertMockTeacherIfNotExists("Frontend", "Supervisor", supervisorEmail, "A1.12");
		InsertMockTeacherIfNotExists("Frontend", "Teacher", teacherEmail, "B2.34");
		InsertMockStudentIfNotExists("Frontend", "Student1", student1Email);
		InsertMockStudentIfNotExists("Frontend", "Student2", student2Email);

		Wis2User supervisor = userRepository.findByEmail(supervisorEmail).orElseThrow();
		Wis2User teacher = userRepository.findByEmail(teacherEmail).orElseThrow();
		Wis2User student1 = userRepository.findByEmail(student1Email).orElseThrow();
		Wis2User student2 = userRepository.findByEmail(student2Email).orElseThrow();

		// 2. Create Course
		String courseShortcut = "FEC";
		Set<Wis2User> teachers = new HashSet<>();
		teachers.add(teacher);
		teachers.add(supervisor);

		InsertMockCourseIfNotExists("Frontend Development", 5000.0, courseShortcut, CourseEndType.EXAM, supervisor,
				teachers);

		// Add description
		Course course = courseRepository.findByShortcut(courseShortcut).orElseThrow();
		course.setDescription("This course covers everything about Frontend Development using React and TypeScript.");
		courseRepository.save(course);

		// 3. Enroll Students
		enrollStudentDirectly(course, student1);
		enrollStudentDirectly(course, student2);

		// 4. Create Terms
		// Total points must add up to 100: Lab(20) + Midterm(30) + Final Exam(50) = 100
		
		// Lecture - No points (informational only)
		createTerm(courseShortcut, "Intro to React", TermType.LECTURE, "LEC_1",
				LocalDateTime.now().plusDays(2).withHour(10).withMinute(0), supervisor.getUsername());

		// Lab - 20 points, auto-registered
		createTermWithPoints(courseShortcut, "React Hooks Lab", TermType.LAB, "LAB_A",
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0), supervisor.getUsername(), 20, true);

		// Midterm - 30 points, auto-registered
		createTermWithPoints(courseShortcut, "Midterm Exam", TermType.MIDTERM_EXAM, "LEC_1",
				LocalDateTime.now().plusDays(5).withHour(9).withMinute(0), supervisor.getUsername(), 30, true);

		// Final Exam - 50 points, NOT auto-registered (students need to register manually)
		// Multiple exam slots but students can only register for one
		createTermWithPoints(courseShortcut, "Final Exam - Slot 1", TermType.EXAM, "LEC_1",
				LocalDateTime.now().plusDays(10).withHour(9).withMinute(0), supervisor.getUsername(), 50, false);

		createTermWithPoints(courseShortcut, "Final Exam - Slot 2", TermType.EXAM, "LEC_2",
				LocalDateTime.now().plusDays(10).withHour(14).withMinute(0), supervisor.getUsername(), 50, false);

		createTermWithPoints(courseShortcut, "Final Exam - Slot 3", TermType.EXAM, "LEC_1",
				LocalDateTime.now().plusDays(12).withHour(10).withMinute(0), supervisor.getUsername(), 50, false);

		// 5. Add completed and graded courses for student1 to show impressive history
		enrichStudentWithCompletedCourses(student1);
	}

	/**
	 * Enriches a student with multiple completed and graded courses for presentation purposes.
	 */
	private void enrichStudentWithCompletedCourses(Wis2User student) {
		// Completed courses with excellent grades
		enrollStudentWithGrade(student, "IIS", 92, 1.0, true, false, true, true);
		enrollStudentWithGrade(student, "IDS", 85, 1.5, true, false, true, true);
		enrollStudentWithGrade(student, "IUS", 78, 2.0, true, false, true, false);
		
		// Completed with unit credit (no exam)
		enrollStudentWithGrade(student, "ISA", 88, 1.5, true, false, true, true);
		enrollStudentWithGrade(student, "IZU", 75, 2.0, true, false, true, false);
		
		// Current semester courses - in progress with partial points
		enrollStudentWithGrade(student, "IPK", 45, null, false, false, false, false);
		enrollStudentWithGrade(student, "IOS", 38, null, false, false, false, false);
		enrollStudentWithGrade(student, "IFJ", 52, null, false, false, true, false); // Has unit credit already
		
		// One failed course to show realistic scenario
		enrollStudentWithGrade(student, "IZP", 35, 4.0, false, true, false, false);
	}

	/**
	 * Enrolls a student in a course with specific grade and status for presentation.
	 */
	private void enrollStudentWithGrade(Wis2User student, String courseShortcut, int points, Double finalGrade,
			boolean completed, boolean failed, boolean unitCredit, boolean examPassed) {
		Course course = courseRepository.findByShortcut(courseShortcut).orElse(null);
		if (course == null) {
			return;
		}

		// Check if already enrolled
		boolean alreadyEnrolled = course.getStudentCourses().stream()
				.anyMatch(sc -> sc.getStudent().getId().equals(student.getId()));

		if (!alreadyEnrolled) {
			StudentCourse sc = StudentCourse.builder()
					.student(student)
					.course(course)
					.points(points)
					.finalGrade(finalGrade)
					.completed(completed)
					.failed(failed)
					.unitCredit(unitCredit)
					.examPassed(examPassed)
					.status(RequestStatus.APPROVED)
					.build();
			course.getStudentCourses().add(sc);
			courseRepository.save(course);
		}
	}

	private void enrollStudentDirectly(Course course, Wis2User student) {
		boolean enrolled = course.getStudentCourses().stream()
				.anyMatch(sc -> sc.getStudent().getId().equals(student.getId()));
		if (!enrolled) {
			StudentCourse sc = StudentCourse.builder()
					.student(student)
					.course(course)
					.points(0)
					.completed(false)
					.failed(false)
					.status(RequestStatus.APPROVED)
					.build();
			course.getStudentCourses().add(sc);
			courseRepository.save(course);
		}
	}

	private void createTerm(String courseShortcut, String name, TermType type, String room, LocalDateTime date,
			String supervisorUsername) {
		TermCreationDTO dto = TermCreationDTO.builder()
				.name(name)
				.description("Description for " + name)
				.minPoints(type == TermType.LECTURE ? null : 0)
				.maxPoints(type == TermType.LECTURE ? null : 0)
				.startDate(date)
				.duration(90)
				.autoregister(true)
				.roomShortcut(room)
				.type(type)
				.build();
		try {
			if (type == TermType.EXAM) {
				termService.CreateFinalExam(courseShortcut, dto, supervisorUsername);
			} else {
				termService.CreateNonExamTerm(courseShortcut, dto, supervisorUsername);
			}
		} catch (Exception e) {
			// Ignore if already exists or overlaps (mock data)
			System.out.println("Failed to create term " + name + ": " + e.getMessage());
		}
	}

	private void createTermWithPoints(String courseShortcut, String name, TermType type, String room,
			LocalDateTime date, String supervisorUsername, int maxPoints, boolean autoregister) {
		TermCreationDTO dto = TermCreationDTO.builder()
				.name(name)
				.description(autoregister ? "Auto-registered for all students" : "Students must register manually")
				.minPoints(0)
				.maxPoints(maxPoints)
				.startDate(date)
				.duration(type == TermType.EXAM ? 120 : 90)
				.autoregister(autoregister)
				.roomShortcut(room)
				.type(type)
				.build();
		try {
			if (type == TermType.EXAM) {
				termService.CreateFinalExam(courseShortcut, dto, supervisorUsername);
			} else {
				termService.CreateNonExamTerm(courseShortcut, dto, supervisorUsername);
			}
		} catch (Exception e) {
			// Ignore if already exists or overlaps (mock data)
			System.out.println("Failed to create term " + name + ": " + e.getMessage());
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