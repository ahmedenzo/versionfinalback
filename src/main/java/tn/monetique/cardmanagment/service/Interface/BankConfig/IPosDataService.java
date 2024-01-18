package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;

import java.util.List;

public interface IPosDataService {

    PosData createPosData(PosData posData, Long binId);

    PosData updatePosData(Long posDataId, PosData updatedPosData);

    boolean deletePosData(Long posDataId);

    PosData getPosDataById(Long PosDataId);

    List<PosData> getAllPosData();
}
