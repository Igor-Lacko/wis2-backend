package IIS.wis2_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.Models.User.User;
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
         * Initialize the mock database with test data.
         */
        public void InsertMockUsers() {
                User user1 = User.builder()
                                .firstName("Test")
                                .lastName("User1")
                                .email("testuser1@example.com")
                                .build();

                User user2 = User.builder()
                                .firstName("Test")
                                .lastName("User2")
                                .email("testuser2@example.com")
                                .build();

                User user3 = User.builder()
                                .firstName("Test")
                                .lastName("User3")
                                .email("testuser3@example.com")
                                .build();

                User user4 = User.builder()
                                .firstName("Test")
                                .lastName("User4")
                                .email("testuser4@example.com")
                                .build();

                // Save
                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);
        }
}