package tn.monetique.cardmanagment.Entities.ApplicationDataRecord;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor

public class PBFApplicationDataRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean PBFgenerated = false;
    private String cnt;
    private String PrikeyFiid;
    private String numAccount;
    private String typ="01";
    private String AcctStat;
    private String recTyp="C";
    private Long availBal = 0000L;
    private Long ledgBal = 0000L ;
    private String amtOnHld="000000000000000000";
    private String ovrdrftLmt="0000000000";
    private String lastDepDat="000000";
    private String lastDepAmt="000000000000000";
    private String lastWdlDat="000000";
    private String lastWdlAmt="000000000000000";
    private String crncyCde;
    private String userFld1;
    private String userFldAci;
    private String userFldRegn;
    private String userFldCust;
    private Timestamp createdAt;
    private Timestamp updatedAt;



    @ManyToOne
    @JoinColumn(name = "pos_pbf_xd_id")
    private POSPBFXD pospbfxd;

    @OneToOne
    @JoinColumn(name = "card_holder_id")
    private CardHolder pbfCardHolder;


    @OneToMany(mappedBy = "pbfApplicationDataRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PBFBalanceHistory> balanceHistories;

    ///////For Stucture///////


    public PBFApplicationDataRecord() {

    }
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
    @PreUpdate
    protected void onUpdate() {

        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
