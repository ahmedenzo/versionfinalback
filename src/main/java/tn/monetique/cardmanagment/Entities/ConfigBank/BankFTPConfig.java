package tn.monetique.cardmanagment.Entities.ConfigBank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
public class BankFTPConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String server;
    private int port;
    private String username;
    private String password;
    private String remotePath;

    public BankFTPConfig() {

    }
    @OneToOne(mappedBy = "bankFTPConfig")
    private Bank bank;
}

