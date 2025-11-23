package IIS.wis2_backend.Services.Account;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.DTO.Request.Auth.LoginDTO;
import IIS.wis2_backend.DTO.Request.Auth.RegisterDTO;
import IIS.wis2_backend.DTO.Response.User.RegisterResponseDTO;
import IIS.wis2_backend.DTO.Response.User.UserDTO;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.UserAlreadyExistsException;
import IIS.wis2_backend.Models.Tokens.RefreshToken;
import IIS.wis2_backend.Repositories.Tokens.RefreshTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Services.UserService;
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
     * Refresh token repository.
     */
    private final RefreshTokenRepository refreshTokenRepository;

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
     * Refresh token expiration time in milliseconds.
     */
    @Value("${refresh.expirationMs}")
    private long refreshExpirationMs;

    /**
     * JWT token expiration time in milliseconds.
     */
    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    /**
     * Constructor for AuthService.
     * 
     * @param userService              User service to create users, get user
     *                                 details,
     *                                 etc.
     * @param passwordEncoder          Password encoder to hash passwords.
     * @param authenticationManager    Authentication manager to authenticate users.
     * @param jwtUtils                 JWT utility class to generate tokens.
     * @param accountActivationService Service to handle account activations on
     *                                 registration.
     * @param refreshTokenRepository   Refresh token repository.
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JWTUtils jwtUtils, UserService userService,
            AccountActivationService accountActivationService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.accountActivationService = accountActivationService;
        this.refreshTokenRepository = refreshTokenRepository;
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
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .build();
    }

    /**
     * Tries to log in a user.
     * 
     * @param loginDTO DTO containing the login details.
     * @return JWT token if login is successful.
     */
    public Pair<ResponseCookie, ResponseCookie> LoginUser(LoginDTO loginDTO) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        String jwt = jwtUtils.generateToken(loginDTO.getUsername());
        String refreshToken = GenerateRefreshToken(loginDTO.getUsername());

        // Make cookies (also known as baking)
        ResponseCookie jwtCookie = ResponseCookie.from("JWT", jwt)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtExpirationMs / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", refreshToken)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshExpirationMs / 1000)
                .build();

        return Pair.of(jwtCookie, refreshCookie);
    }

    /**
     * Returns a user's role.
     * 
     * @param username the username of the user.
     * @return the role of the user.
     */
    public String GetUserRole(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getRole().name())
                // This shouldn't happen since it's after authentication
                .orElseThrow(() -> new NotFoundException("This user doesn't exist!"));
    }

    /**
     * Gets a refresh token and generates a new JWT token.
     * 
     * @param refreshToken The refresh token.
     * @return A pair of (new JWT token cookie, new refresh token cookie).
     */
    public Pair<ResponseCookie, ResponseCookie> RefreshJWTToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new IllegalArgumentException("Refresh token has expired.");
        }

        String username = token.getUser().getUsername();

        // Rotate tokens
        refreshTokenRepository.delete(token);

        String newJwtToken = jwtUtils.generateToken(username);
        String newRefreshToken = GenerateRefreshToken(username);

        ResponseCookie jwtCookie = ResponseCookie.from("JWT", newJwtToken)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtExpirationMs / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH", newRefreshToken)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshExpirationMs / 1000)
                .build();

        return Pair.of(jwtCookie, refreshCookie);
    }

    /**
     * Logs out a user by deleting their refresh token.
     * 
     * @param refreshToken The refresh token to delete.
     */
    public void Logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Generates a new refresh token for a user.
     * 
     * @param username The username of the user.
     * @return The generated refresh token.
     */
    private String GenerateRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByUsername(username).orElseThrow(
                        () -> new NotFoundException("User not found for refresh token generation")))
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .token(UUID.randomUUID().toString())
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    /**
     * Deletes all expired refresh tokens each midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void DeleteExpiredRefreshTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
}