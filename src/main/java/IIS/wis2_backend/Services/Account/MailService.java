package IIS.wis2_backend.Services.Account;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Service for sending account activation and password reset emails.
 */
@Service
public class MailService {
    /**
     * Env indicating the "from" field in emails.
     */
    @Value("${spring.mail.sender}")
    private String senderAddress;

    /**
     * Mail sender instance.
     */
    private final JavaMailSender mailSender;

    /**
     * Activation subject.
     */
    private static final String activationSubject = "WIS2: Account activation";

    /**
     * Constructor for MailService.
     *
     * @param mailSender the JavaMailSender instance
     */
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an account activation email.
     *
     * @param recipient the email address to send the activation link to.
     * @param username  the username of the recipient.
     * @param activationLink the activation link to include in the email.
     */
    public void SendActivationEmail(String recipient, String username, String activationLink) {
        // Create the message
        SimpleMailMessage message = new SimpleMailMessage();

        // Fill it out
        message.setTo(recipient);
        message.setFrom(senderAddress);
        message.setSubject(activationSubject);
        message.setText("Your WIS2 username is: " + username + "\n\n"
                + "You can activate your account by clicking the following link: " + activationLink);

        // And send it!
        mailSender.send(message);
    }

    /**
     * Sends a password reset email.
     *
     * @param recipient the email address to send the password reset link to.
     * @param passwordResetLink the password reset link to include in the email.
     */
    public void SendPasswordResetMail(String recipient, String passwordResetLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipient);
        message.setFrom(senderAddress);
        message.setSubject("WIS2: Password reset");
        message.setText("You can reset your password by clicking the following link: " + passwordResetLink);

        mailSender.send(message);
    }
}