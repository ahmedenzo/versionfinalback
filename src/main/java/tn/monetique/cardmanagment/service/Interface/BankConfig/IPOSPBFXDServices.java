package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;

import java.util.List;

public interface IPOSPBFXDServices {

    POSPBFXD createPOSPBFXD(POSPBFXD posPbfXd, Long binId);

    POSPBFXD updatePOSPBFXD(Long posPbfXdId, POSPBFXD updatedPOSPBFXD);

    boolean deletePOSPBFXD(Long posPbfXdId);

    List<POSPBFXD> getAllPOSPBFXDs();

    POSPBFXD getpospbfbyid(Long posPbfXdId);
}
