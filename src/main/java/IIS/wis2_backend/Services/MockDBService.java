package IIS.wis2_backend.Services;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import IIS.wis2_backend.Repositories.CourseRepository;
import IIS.wis2_backend.Repositories.User.StudentRepository;
import IIS.wis2_backend.Repositories.User.TeacherRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import jakarta.annotation.PostConstruct;

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
	 * Constructor for MockDBService.
	 * 
	 * @param teacherRepository Teacher repository.
	 * @param studentRepository Student repository.
	 * @param courseRepository  Course repository.
	 */
	@Autowired
	public MockDBService(UserRepository userRepository, TeacherRepository teacherRepository,
			StudentRepository studentRepository,
			CourseRepository courseRepository) {
		this.userRepository = userRepository;
		this.teacherRepository = teacherRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	/**
	 * Inserts a teacher into the mock db if he doesn't exist yet.
	 * 
	 * @param firstName First name of the teacher.
	 * @param lastName  Last name of the teacher.
	 * @param email     Email of the teacher.
	 */
	private void InsertMockTeacherIfNotExists(String firstName, String lastName, String email) {
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
					.build();

			teacherRepository.save(teacher);
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

			studentRepository.save(student);
		}
	}

	/**
	 * Inserts a course into the mock db if it doesn't exist yet.
	 * 
	 * @param name     Name of the course.
	 * @param price    Price of the course.
	 * @param shortcut Shortcut of the course.
	 */
	private void InsertMockCourseIfNotExists(String name, Double price, String shortcut, CourseEndType endType) {
		if (!courseRepository.existsByShortcut(shortcut)) {
			Course course = Course.builder()
					.name(name)
					.price(price)
					.shortcut(shortcut)
					.completedBy(endType)
					.build();

			courseRepository.save(course);
		}
	}

	/**
	 * Runs all the InsertMockX methods.
	 */
	@PostConstruct
	@Transactional
	public void InitializeMockDB() {
		InsertMockStudents();
		InsertMockTeachers();
		InsertMockCourses();
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
		InsertMockTeacherIfNotExists("Petr", "Pavel", "ppavel@fit.vutbr.cz");
		InsertMockTeacherIfNotExists("John", "Doe", "jdoe@fit.vutbr.cz");
		InsertMockTeacherIfNotExists("Alice", "Johnson", "alice.johnson@example.com");
		InsertMockTeacherIfNotExists("Martin", "Smith", "martin.smith@example.com");
		InsertMockTeacherIfNotExists("Eva", "Brown", "eva.brown@example.com");
		InsertMockTeacherIfNotExists("Thomas", "Cooper", "thomas.cooper@example.com");
		InsertMockTeacherIfNotExists("Clara", "Taylor", "clara.taylor@example.com");
		InsertMockTeacherIfNotExists("Lucas", "White", "lucas.white@example.com");
		InsertMockTeacherIfNotExists("Paul", "Davis", "paul.davis@example.com");
		InsertMockTeacherIfNotExists("Monica", "Green", "monica.green@example.com");
		InsertMockTeacherIfNotExists("Robert", "Baker", "robert.baker@example.com");
		InsertMockTeacherIfNotExists("Veronica", "Hall", "veronica.hall@example.com");
	}

	/**
	 * Initialize the mock database with test courses.
	 */
	public void InsertMockCourses() {
		InsertMockCourseIfNotExists("Information Systems", 1500.0, "IIS", CourseEndType.EXAM);
		InsertMockCourseIfNotExists("Database Systems", 1200.0, "IDS", CourseEndType.UNIT_CREDIT);
		InsertMockCourseIfNotExists("Introduction to Software Engineering", 1300.0, "IUS", CourseEndType.GRADED_UNIT_CREDIT);
		InsertMockCourseIfNotExists("Computer Communications and Networks", 1400.0, "IPK", CourseEndType.EXAM);
		InsertMockCourseIfNotExists("Network Applications and Network Administration", 1600.0, "ISA", CourseEndType.UNIT_CREDIT_EXAM);
		InsertMockCourseIfNotExists("Operating Systems", 10000.0, "IOS", CourseEndType.GRADED_UNIT_CREDIT);
		InsertMockCourseIfNotExists("Formal Languages and Compilers", 1100.0, "IFJ", CourseEndType.EXAM);
		InsertMockCourseIfNotExists("Introduction to Artificial Intelligence", 1700.0, "IZU", CourseEndType.UNIT_CREDIT);
	}

	/**
	 * Clear the mock database.
	 */
	public void ClearMockDB() {
		userRepository.deleteAll();
		courseRepository.deleteAll();
	}
}