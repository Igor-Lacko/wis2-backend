package IIS.wis2_backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.LinkTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

/**
 * Service for handling password reset functionality.
 */
@Service
public class PasswordResetService {
    /**
     * Base URL for password reset links prepended to /reset-password?token=....
     */
    @Value("${server.url}")
    private String serverUrl;

    /**
     * The expiration time for password reset links in minutes.
     */
    @Value("${password.reset.link.expiration.minutes}")
    private int expirationTimeInMinutes;

    /**
     * Repository for password reset tokens.
     */
    private final LinkTokenRepository passwordResetTokenRepository;

    /**
     * Repository for users.
     */
    private final UserRepository userRepository;

    /**
     * Service for sending password reset emails.
     */
    private final MailService mailService;

    /**
     * Constructor for PasswordResetService.
     * 
     * @param passwordResetTokenRepository Repository for password reset tokens.
     */
    @Autowired
    public PasswordResetService(LinkTokenRepository passwordResetTokenRepository, UserRepository userRepository,
            MailService mailService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * Creates a password reset link, sends a mail to the user and stores the token
     * in the database.
     * 
     * @param email The email of the user requesting a password reset.
     */
    public void ResetPassword(String email) {
        Wis2User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with the given email does not exist."));
    }
}