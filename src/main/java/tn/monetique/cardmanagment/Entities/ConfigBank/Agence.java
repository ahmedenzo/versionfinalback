package tn.monetique.cardmanagment.Entities.ConfigBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
public class Agence implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agenceId;
    @Column(unique = true)
    private String agenceName;
    private String agenceAdresse;
    private String branchCode;
    private String contactEmail;
    private String contactPhone ;
    private String cityCode;
    private Date openingDate;
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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
}
