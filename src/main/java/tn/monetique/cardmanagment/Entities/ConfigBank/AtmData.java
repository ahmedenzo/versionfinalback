package tn.monetique.cardmanagment.Entities.ConfigBank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class
AtmData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long atmDataId;
    private String lgth;
    private String useLimit;
    private String totalWithdrawalLimit;
    private String offlineWithdrawalLimit;
    private String totalCashAdvanceLimit;
    private String offlineCashAdvanceLimit;
    private String maximumDepositCreditAmount;
    private String lastUsed;
    private String issuerTransactionProfile;



    public AtmData() {

    }
    @OneToOne
    @JoinColumn(name = "binid_id")
    private Bin bin;
}

