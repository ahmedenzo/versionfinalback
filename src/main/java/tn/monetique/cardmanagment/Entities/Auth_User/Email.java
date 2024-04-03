package tn.monetique.cardmanagment.Entities.Auth_User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private String from ="hamza.melki@monetiquetunisie.com";
    private String to;
    private String subject;
    private String content;
}