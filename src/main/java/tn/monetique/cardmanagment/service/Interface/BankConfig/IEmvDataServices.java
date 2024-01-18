package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;

public interface IEmvDataServices {


    EmvData CreateEmvData(EmvData emvData, Long binId);

    EmvData updateEmvData(Long emvDataId, EmvData updatedEmvData);

    EmvData getEmvDataById(Long EmvDataId);
}
