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
public class PosData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posDataId;
    private String segxLgth="0140000000000000";
    private String totalPurchaseLimit;
    private String offlinePurchaseLimit;
    private String totalCashAdvanceLimit;
    private String offlineCashAdvanceLimit;
    private String totalWithdrawalLimit;
    private String offlineWithdrawalLimit;
    private String useLimit="0010";
    private String totalRefundCreditLimit;
    private String offlineRefundCreditLimit;
    private String reasonCode;
    private String lastUsed;
    private String userField2;
    private String issuerTransactionProfile;


    public PosData() {

    }
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "binid_id")
    private Bin bin;
}