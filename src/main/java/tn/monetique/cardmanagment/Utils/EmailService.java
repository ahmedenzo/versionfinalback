package tn.monetique.cardmanagment.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.Email;
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(final Email email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(email.getFrom());
            System.out.println(email.getFrom());
            message.setSubject(email.getSubject());
            message.setText(email.getContent());
            message.setTo(email.getTo());
            System.out.println(email.getTo());
            javaMailSender.send(message);
            logger.info("Simple email sent successfully to {}", email.getTo());
        } catch (MailException e) {
            logger.error("Failed to send simple email to {}: {}", email.getTo(), e.getMessage());
            // You might handle the exception according to your application's requirements
        }
    }

    public void sendConfirmationEmail(String recipientEmail, String confirmationUrl) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipientEmail);
            msg.setSubject("Confirm your email address");
            String message = "Please click the link below to confirm your email address:\n" + confirmationUrl;
            msg.setText(message);

            javaMailSender.send(msg);
            logger.info("Confirmation email sent successfully to {}", recipientEmail);
        } catch (MailException e) {
            logger.error("Failed to send confirmation email to {}: {}", recipientEmail, e.getMessage());
            // You might handle the exception according to your application's requirements
        }
    }
}
