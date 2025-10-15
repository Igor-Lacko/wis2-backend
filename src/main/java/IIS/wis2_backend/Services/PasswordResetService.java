package IIS.wis2_backend.Services;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import IIS.wis2_backend.DTO.PasswordResetDTO;
import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.DoesntMatchException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.LinkExpiredException;
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
     * Password encoder to hash newly set passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for PasswordResetService.
     * 
     * @param passwordResetTokenRepository Repository for password reset tokens.
     */
    @Autowired
    public PasswordResetService(LinkTokenRepository passwordResetTokenRepository, UserRepository userRepository,
            MailService mailService, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a password reset link, sends a mail to the user and stores the token
     * in the database.
     * 
     * @param email The email of the user requesting a password reset.
     */
    public void SetupPasswordReset(String email) {
        Wis2User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with the given mail doesn't exist."));

        String linkToken = LinkTokenUtils.GenerateLinkToken();

        LinkToken passwordResetToken = LinkToken.builder()
                .tokenHash(LinkTokenUtils.HashToken(linkToken))
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

    /**
     * Tries to reset the password corresponding to token.
     * @param passwordResetDTO Contains the token, password and reset password.
     */
    public void ResetPassword(PasswordResetDTO passwordResetDTO) {
        String tokenHash = LinkTokenUtils.HashToken(passwordResetDTO.token());

        // Find the token
        LinkToken passwordResetToken = passwordResetTokenRepository.findByTokenHashAndType(tokenHash, LinkTokenType.PASSWORD_RESET)
                .orElseThrow(() -> new NotFoundException("The given token doesn't exist."));

        // Check if the token is expired
        Date now = new Date(System.currentTimeMillis());
        if (passwordResetToken.getExpirationDate().before(now)) {
            throw new LinkExpiredException("This link has expired", false);
        }

        // Check if the passwords match
        if (!passwordResetDTO.password().equals(passwordResetDTO.confirmPassword())) {
            throw new DoesntMatchException("The passwords do not match.");
        }

        // Update the user's password
        Wis2User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetDTO.password()));
        userRepository.save(user);
    }
}