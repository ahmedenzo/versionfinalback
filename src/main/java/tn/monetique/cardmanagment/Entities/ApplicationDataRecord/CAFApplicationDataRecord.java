package tn.monetique.cardmanagment.Entities.ApplicationDataRecord;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.ConfigBank.*;

import java.io.Serializable;
import java.sql.Timestamp;


@Entity
    @Getter
    @Setter
    @AllArgsConstructor
    public class CAFApplicationDataRecord implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private boolean CFAgenerated = false;
        private String count;
        private String lgth;
        private String pan;
        private String MbrNum;
        private String recordType;
        private String cardType;
        private String fiid;
        private String cardStatus;
        private String pinOfset;
        private String totalWithdrawalLimit;
        private String offlineWithdrawalLimit;
        private String totalCashAdvanceLimit;
        private String offlineCashAdvanceLimit;
        private String aggregateLimit;
        private String offlineAggregateLimit;
        private String firstUsedDate;
        private String lastResetDate;
        private String cardExpDate;
        private String cardEffectiveDate;
        private String userField1;
        private String secondCardExpirationDate;
        private String secondCardEffectiveDate;
        private String secondCardStatus;
        private String userField2;
        private String userFieldACI;
        private String userFieldREGN;
        private String userFieldCUST;
        private String acctLgth;
        private String acctCnt;
        private String acctTyp;
        private String acctNum;
        private String acctStat;
        private String acctDescr;
        private String acctCorp;
        private String acctQual;
        private Timestamp createdAt;
    private Timestamp updatedAt;

        @ManyToOne
        @JoinColumn(name = "atm_data_id")
        private AtmData atmData;
        @ManyToOne
        @JoinColumn(name = "pos_data_id")
        private PosData posData;
        @ManyToOne
        @JoinColumn(name = "emv_data_id")
        private EmvData emvData;
        @OneToOne
        @JoinColumn(name = "card_holder_id")
        private CardHolder cafCardHolder;

        public CAFApplicationDataRecord() {

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
