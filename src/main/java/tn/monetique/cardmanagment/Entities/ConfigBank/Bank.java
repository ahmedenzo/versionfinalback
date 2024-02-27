package tn.monetique.cardmanagment.Entities.ConfigBank;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Bank implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;
    @Column(unique = true)
    private String bankName;
    private String bankIdCode;
    private String bankLocation;
    private String countryCode;
    private String contactEmail;
    private String contactPhone;
    private String mainOfficeAddress;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private String updatedBy;


    @PreUpdate
    protected void onUpdate() {

        updatedAt = new Timestamp(System.currentTimeMillis());

    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ftp_config_id", referencedColumnName = "id")
    private BankFTPConfig bankFTPConfig;
}
