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
         * Initialize the mock database with test data.
         */
        public void InsertMockUsers() {
                Wis2User user1 = Wis2User.builder()
                                .firstName("Test")
                                .lastName("User1")
                                .email("testuser1@example.com")
                                .build();

                Wis2User user2 = Wis2User.builder()
                                .firstName("Test")
                                .lastName("User2")
                                .email("testuser2@example.com")
                                .build();

                Wis2User user3 = Wis2User.builder()
                                .firstName("Test")
                                .lastName("User3")
                                .email("testuser3@example.com")
                                .build();

                Wis2User user4 = Wis2User.builder()
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