package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.configentities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.PosDataRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPosDataService;

import java.util.List;
import java.util.Optional;

@Service
public class PosDataService implements IPosDataService {
    @Autowired
    private PosDataRepository posDataRepository;
    @Autowired
    private BinRepository binRepository;


    @Override
    public PosData createPosData(PosData posData, Long binId) {
        Bin bin = binRepository.findById(binId).orElse(null);
        if (bin != null) {
            posData.setBin(bin);
            return posDataRepository.save(posData);
        }
        return posData;
    }


    @Override
    public PosData updatePosData(Long posDataId, PosData updatedPosData) {
        Optional<PosData> posDataOptional = posDataRepository.findById(posDataId);
        if (posDataOptional.isPresent()) {
            PosData existingPosData = posDataOptional.get();
            existingPosData.setSegxLgth(updatedPosData.getSegxLgth());
            existingPosData.setTotalPurchaseLimit(updatedPosData.getTotalPurchaseLimit());
            existingPosData.setOfflinePurchaseLimit(updatedPosData.getOfflinePurchaseLimit());
            existingPosData.setTotalCashAdvanceLimit(updatedPosData.getTotalCashAdvanceLimit());
            existingPosData.setOfflineCashAdvanceLimit(updatedPosData.getOfflineCashAdvanceLimit());
            existingPosData.setTotalWithdrawalLimit(updatedPosData.getTotalWithdrawalLimit());
            existingPosData.setOfflineWithdrawalLimit(updatedPosData.getOfflineWithdrawalLimit());
            existingPosData.setUseLimit(updatedPosData.getUseLimit());
            existingPosData.setTotalRefundCreditLimit(updatedPosData.getTotalRefundCreditLimit());
            existingPosData.setOfflineRefundCreditLimit(updatedPosData.getOfflineRefundCreditLimit());
            existingPosData.setReasonCode(updatedPosData.getReasonCode());
            existingPosData.setLastUsed(updatedPosData.getLastUsed());
            existingPosData.setUserField2(updatedPosData.getUserField2());
            existingPosData.setIssuerTransactionProfile(updatedPosData.getIssuerTransactionProfile());


            // Set the associated bank (if needed)
            existingPosData.setBin(existingPosData.getBin());

            return posDataRepository.save(existingPosData);
        } else {
            return null;
        }

    }

    @Override
    public boolean deletePosData(Long posDataId) {
        {
            if (posDataRepository.existsById(posDataId)) {
                posDataRepository.deleteById(posDataId);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public PosData getPosDataById(Long PosDataId) {
        return posDataRepository.findById(PosDataId).orElse(null);
    }

    @Override
    public List<PosData> getAllPosData() {
        return posDataRepository.findAll();
    }

}
