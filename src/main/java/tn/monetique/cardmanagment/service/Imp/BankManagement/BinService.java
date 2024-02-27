package tn.monetique.cardmanagment.service.Imp.BankManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IbinService;

import java.util.List;
import java.util.Optional;

@Service
public class BinService implements IbinService {
    @Autowired
    BinRepository binRepository;
    @Autowired
    BankRepository bankRepository;

    @Override
    public Bin creatBIn(Bin bin, String bankname) {
        Bank bank = bankRepository.findByBankName(bankname).orElse(null);
        if (bank != null) {
            bin.setBank(bank);
            return binRepository.save(bin);
        }
        return bin;
    }

    @Override
    public Bin Updatebin(Bin updatedbin, Long binId) {
        Optional<Bin> binOptional = binRepository.findById(binId);
        if (binOptional.isPresent()) {
            Bin existingbin = binOptional.get();
            // Update agency properties as needed
            existingbin.setBinValue(updatedbin.getBinValue());
            existingbin.setMoneyCode(updatedbin.getMoneyCode());
            existingbin.setExpireRange(updatedbin.getExpireRange());
            existingbin.setBank(existingbin.getBank());
            existingbin.setCardBrand(updatedbin.getCardBrand());
            existingbin.setCardType(updatedbin.getCardType());
            existingbin.setCodeType(updatedbin.getCodeType());
            existingbin.setMaxbalance(updatedbin.getMaxbalance());
            existingbin.setCurrency(updatedbin.getCurrency());


            return binRepository.save(existingbin);
        } else {
            return null;
        }
    }


    @Override
    public List getbinbybank(String bankName) {
        return binRepository.findBinByBank_BankName(bankName);
    }

    @Override
    public boolean deletebin (Long binId) {
        if (binRepository.existsById(binId)) {
            binRepository.deleteById(binId);
            return true;
        } else {
            return false; // bin not found
        }
    }
    @Override
    public List<Bin> getallbins() {
        return binRepository.findAll();
    }
    @Override
    public  boolean binsofbank(Long bankId){
        List<Bin> binsToDelete = binRepository.findAgencesByBank_BankId(bankId);

        if(binsToDelete.isEmpty()){
            return false;

        } else {
            binRepository.deleteAll(binsToDelete);
            return true;

        }
    }

}
