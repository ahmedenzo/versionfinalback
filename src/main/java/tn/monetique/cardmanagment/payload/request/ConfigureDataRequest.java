package tn.monetique.cardmanagment.payload.request;

import lombok.Data;
import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;
import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;
import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;

@Data
public class ConfigureDataRequest {

    private AtmData atmData;
    private PosData posData;
    private POSPBFXD pospbfxd;
    private EmvData emvData;

}
