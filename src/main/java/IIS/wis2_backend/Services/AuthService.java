package IIS.wis2_backend.Services;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.User.RegisterDTO;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.User.UserRepository;
import jakarta.transaction.Transactional;

/**
 * Service for authentication related operations.
 */
@Service
@Transactional
public class AuthService implements UserDetailsService {
    /**
     * User repository to create users, get user details, etc.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder to hash passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for AuthService.
     * 
     * @param userService     User service to create users, get user details, etc.
     * @param passwordEncoder Password encoder to hash passwords.
     */
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Tries to register a new user.
     * 
     * @param registerDTO DTO containing the registration details.
     * @return UserDTO of the newly registered user.
     */
    public Wis2User RegisterUser(RegisterDTO registerDTO) {
        // Check for same email
        String email = registerDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email, true);
        }

        // Create user
        Wis2User user = Wis2User.builder()
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Wis2User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        String[] authorities = user.getRole() == Roles.ADMIN ? 
                new String[] { "ROLE_ADMIN", "ROLE_USER" } : 
                new String[] { "ROLE_USER" };

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}