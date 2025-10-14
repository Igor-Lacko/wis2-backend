package IIS.wis2_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Auth.LoginDTO;
import IIS.wis2_backend.DTO.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.User.RegisterResponseDTO;
import IIS.wis2_backend.DTO.User.UserDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Utils.JWTUtils;
import jakarta.transaction.Transactional;

/**
 * Service for authentication related operations.
 */
@Service
@Transactional
public class AuthService {
    /**
     * User repository to create users, get user details, etc.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder to hash passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentication manager to authenticate users.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWT utility class to generate tokens.
     */
    private final JWTUtils jwtUtils;

    /**
     * User service to convert users to DTOs, etc.
     */
    private final UserService userService;

    /**
     * Service to handle account activations on registration.
     */
    private final AccountActivationService accountActivationService;

    /**
     * Constructor for AuthService.
     * 
     * @param userService           User service to create users, get user details,
     *                              etc.
     * @param passwordEncoder       Password encoder to hash passwords.
     * @param authenticationManager Authentication manager to authenticate users.
     * @param jwtUtils              JWT utility class to generate tokens.
     */
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JWTUtils jwtUtils, UserService userService,
            AccountActivationService accountActivationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.accountActivationService = accountActivationService;
    }

    /**
     * Tries to register a new user.
     * 
     * @param registerDTO DTO containing the registration details.
     * @return UserDTO of the newly registered user.
     */
    public RegisterResponseDTO RegisterUser(RegisterDTO registerDTO) {
        // Check for same email
        String email = registerDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email, true);
        }

        registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        UserDTO newUser = userService.CreateUser(registerDTO);

        // Send activation email
        accountActivationService.CreateActivationForUser(newUser.getId());

        return RegisterResponseDTO.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .build();
    }

    /**
     * Tries to log in a user.
     * 
     * @param loginDTO DTO containing the login details.
     * @return JWT token if login is successful.
     */
    public String LoginUser(LoginDTO loginDTO) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        return jwtUtils.generateToken(loginDTO.getUsername());
    }
}