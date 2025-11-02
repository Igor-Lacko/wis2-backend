package IIS.wis2_backend.Services;

import java.sql.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Room.Office;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.Room.OfficeRepository;
import IIS.wis2_backend.Repositories.User.StudentRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * Mock database service for testing purposes.
 */
@Service
@Profile("dev")
public class MockDBService {
	/**
	 * User repository to clear users/check for existence.
	 */
	private final UserRepository userRepository;

	/**
	 * Teacher repository to create test teachers.
	 */
	private final TeacherRepository teacherRepository;

	/**
	 * Student repository to create test students.
	 */
	private final StudentRepository studentRepository;

	/**
	 * Course repository to create test courses.
	 */
	private final CourseRepository courseRepository;

	/**
	 * Office repository to give teachers.
	 */
	private final OfficeRepository officeRepository;

	/**
	 * Constructor for MockDBService.
	 * 
	 * @param teacherRepository Teacher repository.
	 * @param studentRepository Student repository.
	 * @param courseRepository  Course repository.
	 */
	@Autowired
	public MockDBService(UserRepository userRepository, TeacherRepository teacherRepository,
			StudentRepository studentRepository,
			CourseRepository courseRepository, OfficeRepository officeRepository) {
		this.userRepository = userRepository;
		this.teacherRepository = teacherRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.officeRepository = officeRepository;
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
			Teacher teacher = Teacher.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("1980-01-01"))
					.email(email)
					.password("pwd")
					.activated(true)
					.role(Roles.USER)
					.office(teacherOffice)
					.build();

			if (teacher != null) {
				teacherRepository.save(teacher);
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
			Student student = Student.builder()
					.firstName(firstName)
					.lastName(lastName)
					.username(firstName.toLowerCase() + "." + lastName.toLowerCase())
					.birthday(Date.valueOf("2000-01-01"))
					.password("pwd")
					.email(email)
					.activated(true)
					.role(Roles.USER)
					.build();

			if (student != null) {
				studentRepository.save(student);
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
	private void InsertMockCourseIfNotExists(String name, Double price, String shortcut, CourseEndType endType,
			Teacher supervisor, Set<Teacher> teachers) {
		if (!courseRepository.existsByShortcut(shortcut)) {
			Course course = Course.builder()
					.name(name)
					.price(price)
					.shortcut(shortcut)
					.completedBy(endType)
					.supervisor(supervisor)
					.teachers(teachers)
					.build();

			if (course == null) {
				return;
			}

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
					.build();

			if (office == null) {
				return;
			}
			officeRepository.save(office);
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
		Teacher pavel = teacherRepository.findAll().stream()
				.filter(t -> "ppavel@fit.vutbr.cz".equals(t.getEmail())).findFirst().orElse(null);
		Teacher jdoe = teacherRepository.findAll().stream()
				.filter(t -> "jdoe@fit.vutbr.cz".equals(t.getEmail())).findFirst().orElse(null);
		Teacher alice = teacherRepository.findAll().stream()
				.filter(t -> "alice.johnson@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher martin = teacherRepository.findAll().stream()
				.filter(t -> "martin.smith@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher eva = teacherRepository.findAll().stream()
				.filter(t -> "eva.brown@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher thomas = teacherRepository.findAll().stream()
				.filter(t -> "thomas.cooper@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher clara = teacherRepository.findAll().stream()
				.filter(t -> "clara.taylor@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher lucas = teacherRepository.findAll().stream()
				.filter(t -> "lucas.white@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher paul = teacherRepository.findAll().stream()
				.filter(t -> "paul.davis@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher monica = teacherRepository.findAll().stream()
				.filter(t -> "monica.green@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher robert = teacherRepository.findAll().stream()
				.filter(t -> "robert.baker@example.com".equals(t.getEmail())).findFirst().orElse(null);
		Teacher veronica = teacherRepository.findAll().stream()
				.filter(t -> "veronica.hall@example.com".equals(t.getEmail())).findFirst().orElse(null);

		// build teacher sets for courses
		java.util.Set<Teacher> iisTeachers = new java.util.HashSet<>();
		if (pavel != null)
			iisTeachers.add(pavel);
		if (alice != null)
			iisTeachers.add(alice);

		java.util.Set<Teacher> idsTeachers = new java.util.HashSet<>();
		if (jdoe != null)
			idsTeachers.add(jdoe);
		if (martin != null)
			idsTeachers.add(martin);

		java.util.Set<Teacher> iusTeachers = new java.util.HashSet<>();
		if (alice != null)
			iusTeachers.add(alice);
		if (eva != null)
			iusTeachers.add(eva);

		java.util.Set<Teacher> ipkTeachers = new java.util.HashSet<>();
		if (martin != null)
			ipkTeachers.add(martin);
		if (thomas != null)
			ipkTeachers.add(thomas);

		java.util.Set<Teacher> isaTeachers = new java.util.HashSet<>();
		if (eva != null)
			isaTeachers.add(eva);
		if (clara != null)
			isaTeachers.add(clara);

		java.util.Set<Teacher> iosTeachers = new java.util.HashSet<>();
		if (thomas != null)
			iosTeachers.add(thomas);
		if (lucas != null)
			iosTeachers.add(lucas);

		java.util.Set<Teacher> ifjTeachers = new java.util.HashSet<>();
		if (clara != null)
			ifjTeachers.add(clara);
		if (paul != null)
			ifjTeachers.add(paul);
		if (veronica != null)
			ifjTeachers.add(veronica);

		java.util.Set<Teacher> izuTeachers = new java.util.HashSet<>();
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
	 * Clear the mock database.
	 */
	public void ClearMockDB() {
		userRepository.deleteAll();
		courseRepository.deleteAll();
	}
}