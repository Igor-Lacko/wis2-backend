package IIS.wis2_backend.Services;

import org.springframework.stereotype.Service;

import IIS.wis2_backend.Utils.MailObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Service for sending account verification and password reset emails.
 */
@Service
public class MailService {
    /**
     * Env indicating the "from" field in emails.
     */
    @Value("${SMTP_SENDER}")
    private String senderAddress;

    /**
     * Mail sender instance.
     */
    private final JavaMailSender mailSender;

    /**
     * Constructor for MailService.
     *
     * @param mailSender the JavaMailSender instance
     */
    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an account verification email.
     *
     * @param recipient the email address to send the verification link to
     */
    public void SendVerificationEmail(String recipient) {
        // Create the message
        SimpleMailMessage message = new SimpleMailMessage();

        // Fill it out
        message.setTo(recipient);
        message.setFrom(senderAddress);
        message.setSubject("WIS2: Account Verification");
        // TODO: verification link
        message.setText("Please verify your account by clicking the following link: [verification link]");

        // And send it!
        mailSender.send(message);
    }
}