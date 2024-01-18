package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.configentities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.AtmDataRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IAtmDataService;

import java.util.Optional;

@Service
public class AtmDataService implements IAtmDataService {
    @Autowired
    private AtmDataRepository atmDataRepository;
    @Autowired
    private BinRepository binRepository;


    @Override
    public AtmData CreateAtmData(AtmData atmData, Long binId) {
        {
            Bin bin = binRepository.findById(binId).orElse(null);
            if (bin != null) {
                atmData.setBin(bin);
                return atmDataRepository.save(atmData);
            }
            return atmData;
        }}


        @Override
        public AtmData updateAtmData(Long atmDataId, AtmData updatedAtmData) {
            Optional<AtmData> atmDataOptional = atmDataRepository.findById(atmDataId);
            if (atmDataOptional.isPresent()) {
                AtmData existingAtmData = atmDataOptional.get();

                // Update the properties of existingAtmData with the values from updatedAtmData
                existingAtmData.setLgth(updatedAtmData.getLgth());
                existingAtmData.setUseLimit(updatedAtmData.getUseLimit());
                existingAtmData.setTotalWithdrawalLimit(updatedAtmData.getTotalWithdrawalLimit());
                existingAtmData.setOfflineWithdrawalLimit(updatedAtmData.getOfflineWithdrawalLimit());
                existingAtmData.setTotalCashAdvanceLimit(updatedAtmData.getTotalCashAdvanceLimit());
                existingAtmData.setOfflineCashAdvanceLimit(updatedAtmData.getOfflineCashAdvanceLimit());
                existingAtmData.setMaximumDepositCreditAmount(updatedAtmData.getMaximumDepositCreditAmount());
                existingAtmData.setLastUsed(updatedAtmData.getLastUsed());
                existingAtmData.setIssuerTransactionProfile(updatedAtmData.getIssuerTransactionProfile());

                // Set the associated bank (if needed)
                existingAtmData.setBin(existingAtmData.getBin());

                return atmDataRepository.save(existingAtmData);
            } else {
                return null; // Handle not found scenario
            }
        }

        // Other service methods


    @Override
    public AtmData getAtmDataById(Long AtmDataId) {
        return atmDataRepository.findById(AtmDataId).orElse(null);
    }
}
