package IIS.wis2_backend.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import IIS.wis2_backend.Enum.LinkTokenType;
import IIS.wis2_backend.Exceptions.ExceptionTypes.AlreadySetException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.LinkExpiredException;
import IIS.wis2_backend.Exceptions.ExceptionTypes.NotFoundException;
import IIS.wis2_backend.Models.LinkToken;
import IIS.wis2_backend.Models.User.Wis2User;
import IIS.wis2_backend.Repositories.LinkTokenRepository;
import IIS.wis2_backend.Repositories.User.UserRepository;
import IIS.wis2_backend.Utils.LinkTokenUtils;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
    @Value("${frontend.url}")
    private String frontendUrl;

    /**
     * Repository for activation tokens.
     */
    private final LinkTokenRepository activationTokenRepository;

    /**
     * User repository to set isActive to true.
     */
    private final UserRepository userRepository;

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
        String tokenHash = LinkTokenUtils.HashToken(token);

        // Try to find the token in the database
        LinkToken activationToken = activationTokenRepository.findByTokenHashAndType(tokenHash, LinkTokenType.ACTIVATION)
                .orElseThrow(() -> new NotFoundException("Token has not been found."));

        // Check if the token is expired
        if (Instant.now().isAfter(activationToken.getExpirationDate())) {
            // Delete the token
            activationTokenRepository.delete(activationToken);
            throw new LinkExpiredException("Link has expired.", true);
        }

        // Activate the user
        Wis2User user = activationToken.getUser();
        if (user.isActivated()) {
            // This shouldn't happen since the token should be deleted after use
            throw new AlreadySetException("Account is already activated.");
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
        String token = LinkTokenUtils.GenerateLinkToken();

        // Associate the token with the user
        Wis2User user = userRepository.findById(userId).orElseThrow();
        LinkToken activationToken = LinkToken.builder()
                .tokenHash(LinkTokenUtils.HashToken(token))
                .user(user)
                .expirationDate(Instant.now().plusSeconds(activationLinkExpirationHours * 60 * 60))
                .type(LinkTokenType.ACTIVATION)
                .build();

        activationTokenRepository.save(activationToken);

        // Send activation email
        String activationLink = GenerateActivationLink(token);
        mailService.SendActivationEmail(user.getEmail(), user.getUsername(), activationLink);
    }

    /**
     * Called when a user requests to resend the activation email.
     * 
     * @param email The email to resend the activation email to.
     */
    public void ResendActivationEmail(String email) {
        Wis2User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with the given mail does not exist."));

        // Again, this shouldn't happen.
        if (user.isActivated()) {
            throw new AlreadySetException("Account is already activated.");
        }

        CreateActivationForUser(user.getId());
    }

    /**
     * Generates an activation link with the given token. Assumes the token is
     * already Base64 URL encoded.
     * 
     * @param token The activation token.
     * @return The activation link.
     */
    private String GenerateActivationLink(String token) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .pathSegment("activate", token)
                .build()
                .toUriString();
    }
}
