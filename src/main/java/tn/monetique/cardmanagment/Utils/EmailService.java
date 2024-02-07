package tn.monetique.cardmanagment.Utils;


import tn.monetique.cardmanagment.Entities.Auth_User.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(final Email email)
    {
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(email.getFrom());
        message.setSubject(email.getSubject());
        message.setText(email.getContent());
        message.setTo(email.getTo());
        javaMailSender.send(message);

    }

    public void sendConfirmationEmail(String recipientEmail, String confirmationUrl) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(recipientEmail);
        msg.setSubject("Confirm your email address");
        String message = "Please click the link below to confirm your email address:\n" + confirmationUrl;
        msg.setText(message);

        javaMailSender.send(msg);
    }
}