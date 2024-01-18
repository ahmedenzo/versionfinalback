package tn.monetique.cardmanagment.service.Imp.BankManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.repository.Bank.AgencyRepository;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IagenceService;

import java.util.List;
import java.util.Optional;

@Service
public class agenceservice implements IagenceService {

    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    BankRepository bankRepository;


    @Override
    public Agence createAgence(Agence agence, String bankname) {
            Bank bank = bankRepository.findByBankName(bankname).orElse(null);
            if (bank != null) {
                agence.setBank(bank);
                return agencyRepository.save(agence);
            }
            return agence;
    }
    @Override
    public Agence UpdateAgence(Long agenceId, Agence updatedAgency) {
        Optional<Agence> agencyOptional = agencyRepository.findById(agenceId);
        if (agencyOptional.isPresent()) {
            Agence existingAgency = agencyOptional.get();
            // Update agency properties as needed
            existingAgency.setAgenceName(updatedAgency.getAgenceName());
            existingAgency.setAgenceAdresse(updatedAgency.getAgenceAdresse());
           existingAgency.setContactEmail(updatedAgency.getContactEmail());
           existingAgency.setContactPhone(updatedAgency.getContactPhone());
           existingAgency.setOpeningDate(updatedAgency.getOpeningDate());
            existingAgency.setBank(existingAgency.getBank());

            return agencyRepository.save(existingAgency);
        } else {
            return null;
        }
    }


    @Override
    public List<Agence> getallagencebyBank(String BankName) {
        return agencyRepository.findAgenceByBank_BankName(BankName);
    }

    @Override
    public List<Agence> getallagence() {
        return agencyRepository.findAll();
    }

    @Override
    public boolean deleteAgence(Long agenceId) {
        if (agencyRepository.existsById(agenceId)) {
            agencyRepository.deleteById(agenceId);
            return true;
        } else {
            return false; // Agency not found
        }
    }
    @Override
    public  boolean agencyofbank(Long bankId){
        List<Agence> agenciesToDelete = agencyRepository.findAgencesByBank_BankId(bankId);

        if(agenciesToDelete.isEmpty()){
            return false;

        } else {
            agencyRepository.deleteAll(agenciesToDelete);
            return true;

        }
    }
}