package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;

public interface IAtmDataService {


    AtmData CreateAtmData(AtmData atmData, Long binId);

    AtmData updateAtmData(Long atmDataId, AtmData updatedAtmData);

    AtmData getAtmDataById(Long AtmDataId);
}
