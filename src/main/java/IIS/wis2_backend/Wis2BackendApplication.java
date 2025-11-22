package IIS.wis2_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.Education.Schedule.ScheduleRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

@SpringBootApplication
public class Wis2BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Wis2BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner insertAdmin(UserRepository userRepository, ScheduleRepository scheduleRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByUsername("admin")) {
				Wis2User admin = Wis2User.builder()
						.username("admin")
						.firstName("Admin")
						.lastName("Admin")
						.email("admin@wis2.com")
						.password(passwordEncoder.encode("admin"))
						.birthday(java.sql.Date.valueOf("2000-01-01"))
						.role(Roles.ADMIN)
						.activated(true)
						.build();

				Schedule schedule = Schedule
						.builder()
						.user(admin)
						.build();
				admin.setSchedule(schedule);

				userRepository.save(admin);
			}
		};
	}

}
