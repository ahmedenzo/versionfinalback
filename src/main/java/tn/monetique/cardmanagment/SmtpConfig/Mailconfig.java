package tn.monetique.cardmanagment.SmtpConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class Mailconfig {
    @Autowired
    IsmtpConfigService ismtpConfigService;

        @Bean
        public JavaMailSender javaMailSender() {
            SmtpConfig smtpConfig = ismtpConfigService.getSmtpConfig();
            if (smtpConfig != null) {
                JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                mailSender.setHost(smtpConfig.getHost());
                mailSender.setPort(smtpConfig.getPort());
                mailSender.setUsername(smtpConfig.getUsername());
                mailSender.setPassword(smtpConfig.getPassword());
                // Additional configurations
                return mailSender;
            } else {
                throw new IllegalStateException("SMTP configuration not found.");
            }
        }
    }


