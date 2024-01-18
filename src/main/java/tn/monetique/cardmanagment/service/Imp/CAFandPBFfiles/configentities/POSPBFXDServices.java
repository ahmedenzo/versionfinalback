package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.configentities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.POSPBFXDRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPOSPBFXDServices;

import java.util.List;
import java.util.Optional;

@Service
public class POSPBFXDServices implements IPOSPBFXDServices {
    @Autowired
    POSPBFXDRepository pospbfxdRepository;
    @Autowired
    private BinRepository binRepository;

    @Override
    public POSPBFXD createPOSPBFXD(POSPBFXD posPbfXd, Long binId) {

        Bin bin = binRepository.findById(binId).orElse(null);
        if (bin != null) {
            posPbfXd.setBin(bin);
            return pospbfxdRepository.save(posPbfXd);
        }
        return posPbfXd;
    }


    @Override
    public POSPBFXD updatePOSPBFXD(Long posPbfXdId, POSPBFXD updatedPOSPBFXD) {
        Optional<POSPBFXD> posPbfXdOptional = pospbfxdRepository.findById(posPbfXdId);
        if (posPbfXdOptional.isPresent()) {
            POSPBFXD existingPOSPBFXD = posPbfXdOptional.get();
            existingPOSPBFXD.setSegxLgth(updatedPOSPBFXD.getSegxLgth());
            existingPOSPBFXD.setTtlFloat(updatedPOSPBFXD.getTtlFloat());
            existingPOSPBFXD.setDaysDelinq(updatedPOSPBFXD.getDaysDelinq());
            existingPOSPBFXD.setMonthsActive(updatedPOSPBFXD.getMonthsActive());
            existingPOSPBFXD.setCycle1(updatedPOSPBFXD.getCycle1());
            existingPOSPBFXD.setCycle2(updatedPOSPBFXD.getCycle2());
            existingPOSPBFXD.setCycle3(updatedPOSPBFXD.getCycle3());
            existingPOSPBFXD.setUnknown(updatedPOSPBFXD.getUnknown());
            existingPOSPBFXD.setUserFld2(updatedPOSPBFXD.getUserFld2());

            // Set the associated bank (if needed)
            existingPOSPBFXD.setBin(existingPOSPBFXD.getBin());

            return pospbfxdRepository.save(existingPOSPBFXD);
        } else {
            return null;
        }
    }


    @Override
    public boolean deletePOSPBFXD(Long posPbfXdId) {
        try {
            pospbfxdRepository.deleteById(posPbfXdId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
    @Override
    public List<POSPBFXD> getAllPOSPBFXDs() {
            return pospbfxdRepository.findAll();
    }

    @Override
    public POSPBFXD getpospbfbyid(Long posPbfXdId) {
        POSPBFXD pospbfxd= pospbfxdRepository.findById(posPbfXdId).orElse(null);
        System.out.println(pospbfxd);
        return pospbfxd;
    }
}
