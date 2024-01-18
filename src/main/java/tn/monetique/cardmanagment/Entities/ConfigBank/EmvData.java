package tn.monetique.cardmanagment.Entities.ConfigBank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class EmvData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emvDataId;
    private String segxLgth;
    private String atcLimit;
    private String sendCardBlock;
    private String sendPutData;
    private String velocityLimitsLowerConsecutiveLimit;
    private String userField2;
    private String dataTag;
    private String sendPinUnblock;
    private String sendPinChange;
    private String pinSyncAct;
    private String accessScriptMgmtSubSys;
    private String issApplDataFmt;
    private String actionTableIndex;

    public EmvData() {

    }
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "binid_id")
    private Bin bin;
}
