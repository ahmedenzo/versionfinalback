package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.configentities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.EmvDataRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IEmvDataServices;

import java.util.Optional;

@Service
public class EmvDataService implements IEmvDataServices {
    @Autowired
    EmvDataRepository emvDataRepository;
    @Autowired
    private BinRepository binRepository;

    @Override
    public EmvData CreateEmvData(EmvData emvData, Long binId) {
         {
             Bin bin = binRepository.findById(binId).orElse(null);
             if (bin != null) {
                 emvData.setBin(bin);
                return emvDataRepository.save(emvData);
            }
            return emvData;
        }}
       @Override
        public EmvData updateEmvData(Long emvDataId, EmvData updatedEmvData) {
            Optional<EmvData> emvDataOptional = emvDataRepository.findById(emvDataId);
            if (emvDataOptional.isPresent()) {
                EmvData existingEmvData = emvDataOptional.get();

                // Update the properties of existingEmvData with the values from updatedEmvData
                existingEmvData.setSegxLgth(updatedEmvData.getSegxLgth());
                existingEmvData.setAtcLimit(updatedEmvData.getAtcLimit());
                existingEmvData.setSendCardBlock(updatedEmvData.getSendCardBlock());
                existingEmvData.setSendPutData(updatedEmvData.getSendPutData());
                existingEmvData.setVelocityLimitsLowerConsecutiveLimit(updatedEmvData.getVelocityLimitsLowerConsecutiveLimit());
                existingEmvData.setUserField2(updatedEmvData.getUserField2());
                existingEmvData.setDataTag(updatedEmvData.getDataTag());
                existingEmvData.setSendPinUnblock(updatedEmvData.getSendPinUnblock());
                existingEmvData.setSendPinChange(updatedEmvData.getSendPinChange());
                existingEmvData.setPinSyncAct(updatedEmvData.getPinSyncAct());
                existingEmvData.setAccessScriptMgmtSubSys(updatedEmvData.getAccessScriptMgmtSubSys());
                existingEmvData.setIssApplDataFmt(updatedEmvData.getIssApplDataFmt());
                existingEmvData.setActionTableIndex(updatedEmvData.getActionTableIndex());

                // Set the associated bank (if needed)
                existingEmvData.setBin(existingEmvData.getBin());

                return emvDataRepository.save(existingEmvData);
            } else {
                return null;
            }
        }
    @Override
    public EmvData getEmvDataById(Long EmvDataId) {
        return emvDataRepository.findById(EmvDataId).orElse(null);
    }
}
