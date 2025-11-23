package IIS.wis2_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.User.UserRepository;

@SpringBootApplication
@EnableScheduling
public class Wis2BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Wis2BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner insertAdmin(UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByUsername("admin")) {
				Schedule schedule = Schedule.builder()
						.build();

				Wis2User admin = Wis2User.builder()
						.username("admin")
						.firstName("Admin")
						.lastName("Admin")
						.email("admin@wis2.com")
						.password(passwordEncoder.encode("admin"))
						.birthday(java.sql.Date.valueOf("2000-01-01"))
						.role(Roles.ADMIN)
						.schedule(schedule)
						.activated(true)
						.build();

				schedule.setUser(admin);
				userRepository.save(admin);
			}
		};
	}

}
