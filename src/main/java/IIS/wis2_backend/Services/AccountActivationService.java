package IIS.wis2_backend.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.LinkExpiredException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.LinkTokenException;
import IIS.wis2_backend.Models.LinkToken;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.LinkTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

/**
 * Service for account activation.
 */
@Service
public class AccountActivationService {
    /**
     * The expiration time for activation links in hours.
     */
    @Value("${activation.link.expiration.hours}")
    private int activationLinkExpirationHours;

    /**
     * Base URL for activation links prepended to /activate?token=....
     */
    @Value("${server.url}")
    private String serverUrl;

    /**
     * Repository for activation tokens.
     */
    private final LinkTokenRepository activationTokenRepository;

    /**
     * User repository to set isActive to true.
     */
    private final UserRepository userRepository;

    /**
     * URL encoder instance.
     */
    private static final Base64.Encoder urlEncoder = Base64.getUrlEncoder();

    /**
     * Secure random instance.
     */
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Mail service to send activation emails.
     */
    private final MailService mailService;

    /**
     * Constructor for AccountActivationService.
     * 
     * @param activationTokenRepository Repository for activation tokens.
     */
    @Autowired
    public AccountActivationService(LinkTokenRepository activationTokenRepository,
            UserRepository userRepository, MailService mailService) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * Try to activate an account with the given token.
     * 
     * @param token The token passed in by the user.
     * 
     * @throws LinkTokenException if the token is invalid or expired.
     */
    public void ActivateAccount(String token) {
        // Try to find the token in the database
        LinkToken activationToken = activationTokenRepository.findByTokenAndType(token, LinkTokenType.ACTIVATION)
                .orElseThrow(() -> new LinkTokenException("Invalid activation token.", true, HttpStatus.BAD_REQUEST));

        // Check if the token is expired
        Date issuedAt = activationToken.getIssuedAt();
        Date now = new Date(System.currentTimeMillis());

        long diffInMillies = now.getTime() - issuedAt.getTime();
        long diffInHours = diffInMillies / (1000 * 60 * 60);
        if (diffInHours > activationLinkExpirationHours) {
            // Delete the token
            activationTokenRepository.delete(activationToken);
            throw new LinkExpiredException("Link has expired.", true);
        }

        // Activate the user
        Wis2User user = activationToken.getUser();
        if (user.isActivated()) {
            // This shouldn't happen since the token should be deleted after use
            throw new LinkTokenException("Account is already activated.", true, HttpStatus.CONFLICT);
        }

        user.setActivated(true);
        userRepository.save(user);

        // Delete the token
        activationTokenRepository.delete(activationToken);
    }

    /**
     * Creates a new activation token for the given user and associates them.
     * Called after registering or after the previous token expires.
     * Sends an activation email to the user.
     * 
     * @param userId the ID of the registered user.
     */
    public void CreateActivationForUser(Long userId) {
        String token = GenerateActivationToken();

        // Associate the token with the user
        var user = userRepository.findById(userId).orElseThrow();
        LinkToken activationToken = LinkToken.builder()
                .token(token)
                .user(user)
                .issuedAt(new Date(System.currentTimeMillis()))
                .type(LinkTokenType.ACTIVATION)
                .build();

        activationTokenRepository.save(activationToken);

        // Send activation email
        String activationLink = GenerateActivationLink(token);
        mailService.SendActivationEmail(user.getEmail(), activationLink);
    }

    /**
     * Generates a random 256-bit token.
     * 
     * @return The generated token.
     */
    public String GenerateActivationToken() {
        byte[] randBytes = new byte[32];
        secureRandom.nextBytes(randBytes);
        return urlEncoder.encodeToString(randBytes);
    }

    /**
     * Generates an activation link with the given token. Assumes the token is
     * already Base64 URL encoded.
     * 
     * @param token The activation token.
     * @return The activation link.
     */
    public String GenerateActivationLink(String token) {
        return UriComponentsBuilder.fromUriString(serverUrl)
                .path("/activate")
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    /**
     * Called when a user requests to resend the activation email.
     * 
     * @param email The email to resend the activation email to.
     */
    public void ResendActivationEmail(String email) {
        Wis2User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LinkTokenException("User with the given email does not exist.", true, HttpStatus.NOT_FOUND));

        // Again, this shouldn't happen.
        if (user.isActivated()) {
            throw new LinkTokenException("Account is already activated.", true, HttpStatus.CONFLICT);
        }

        CreateActivationForUser(user.getId());
    }
}
