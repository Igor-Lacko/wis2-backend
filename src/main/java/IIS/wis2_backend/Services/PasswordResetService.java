package IIS.wis2_backend.Services;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.LinkToken;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.LinkTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Utils.LinkTokenUtils;

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
                .orElseThrow(() -> new NotFoundException("User with the given mail doesn't exist."));

        String linkToken = LinkTokenUtils.GenerateLinkToken();

        LinkToken passwordResetToken = LinkToken.builder()
                .token(linkToken)
                .user(user)
                .expirationDate(new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000))
                .type(LinkTokenType.PASSWORD_RESET)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        // Send the email
        String passwordResetLink = GeneratePasswordResetLink(linkToken);
        mailService.SendPasswordResetMail(user.getEmail(), passwordResetLink);
    }

    /**
     * Generates a password reset link with the given token.
     * 
     * @param token The token to be included in the link.
     * @return The complete password reset link.
     */
    private String GeneratePasswordResetLink(String token) {
        return UriComponentsBuilder.fromUriString(serverUrl)
                .path("/reset-password")
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}