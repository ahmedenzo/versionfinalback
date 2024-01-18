package tn.monetique.cardmanagment.payload.response;

import lombok.Data;
import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;
import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;
import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;

@Data
public class ConfigureDataResponse {

        private AtmData configuredAtmData;
        private EmvData configuredEmvData;
        private PosData configuredPosData;
        private POSPBFXD configuredPOSPBFXD;
        private String message;
}
