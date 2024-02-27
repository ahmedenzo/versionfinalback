package tn.monetique.cardmanagment.Entities.ConfigBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Getter
@Setter
public class Bin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long binId;
     private String binValue;
     private int ExpireRange;
     private String MoneyCode;
    private String cardBrand;
    private String cardType;
    private String codeType;
    private String currency;
    private Long maxbalance;
    private String territorycode;
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

    @JsonIgnore
    @OneToOne(mappedBy = "bin", cascade = CascadeType.ALL)
    @JoinColumn()
    private AtmData Atmdata;
    @JsonIgnore
    @OneToOne(mappedBy = "bin", cascade = CascadeType.ALL)
    @JoinColumn()
    private POSPBFXD pospbfxd;
    @JsonIgnore
    @OneToOne(mappedBy = "bin", cascade = CascadeType.ALL)
    @JoinColumn()
    private PosData posData;
    @JsonIgnore
    @OneToOne(mappedBy = "bin", cascade = CascadeType.ALL)
    @JoinColumn()
    private EmvData emvData;
}
