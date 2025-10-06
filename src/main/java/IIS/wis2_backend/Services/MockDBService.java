package IIS.wis2_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * Mock database service for testing purposes.
 */
@Service
public class MockDBService {
	/**
	 * User repo to create test users.
	 */
	private final UserRepository userRepository;

	/**
	 * Constructor for MockDBService.
	 * 
	 * @param userRepository User repository.
	 */
	public MockDBService(@Autowired UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Inserts a user into the mock db if he doesn't exist yet.
	 * 
	 * @param firstName First name of the user.
	 * @param lastName  Last name of the user.
	 * @param email     Email of the user.
	 */
	public void InsertMockUserIfNotExists(String firstName, String lastName, String email) {
		if (!userRepository.existsByEmail(email)) {
			Wis2User user = Wis2User.builder()
					.firstName(firstName)
					.lastName(lastName)
					.email(email)
					.build();
			userRepository.save(user);
		}
	}

	/**
	 * Initialize the mock database with test data.
	 */
	public void InsertMockUsers() {
		InsertMockUserIfNotExists("Igor", "Lacko", "xlackoi00@fit.vutbr.cz");
		InsertMockUserIfNotExists("Jakub", "Kapitulcin", "xkapitj00@fit.vutbr.cz");
		InsertMockUserIfNotExists("Milan", "Babuljak", "xbabulm00@fit.vutbr.cz");
		InsertMockUserIfNotExists("Jaroslav", "Synek", "xsynekj00@fit.vutbr.cz");
		InsertMockUserIfNotExists("Adam", "Bisa", "xbisaad00@fit.vutbr.cz");
	}

	/**
	 * Clear the mock database.
	 */
	public void ClearMockDB() {
		userRepository.deleteAll();
	}
}