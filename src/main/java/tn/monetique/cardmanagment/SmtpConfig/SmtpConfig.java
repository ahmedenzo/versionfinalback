package tn.monetique.cardmanagment.SmtpConfig;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "smtp_config")
@Getter
@Setter
@AllArgsConstructor
public class SmtpConfig {
        @Id
        private Long id=1L;
        private String host;
        private int port;
        private String username;
        private String password;
        private boolean auth;
        private boolean starttls;

        public SmtpConfig() {

        }
}
