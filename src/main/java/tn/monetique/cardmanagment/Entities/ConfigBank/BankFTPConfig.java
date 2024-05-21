package tn.monetique.cardmanagment.Entities.ConfigBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Getter
@Setter
@AllArgsConstructor
public class BankFTPConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String server;
    private int port;
    private String username;
    private String password;
    private String remotePathPorter;
    private String remotePqthCAFPBF;
    public BankFTPConfig() {

    }
    @OneToOne(mappedBy = "bankFTPConfig")
    @JsonIgnore
    private Bank bank;
}

