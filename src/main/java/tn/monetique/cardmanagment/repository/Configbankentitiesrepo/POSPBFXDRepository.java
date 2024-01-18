package tn.monetique.cardmanagment.repository.Configbankentitiesrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;

import java.util.Optional;

@Repository
public interface POSPBFXDRepository extends JpaRepository<POSPBFXD, Long> {
    Optional<POSPBFXD> findById(Long posPbfXdId);
    Optional<POSPBFXD> findByBin_BinId(Long binId);
    Optional<POSPBFXD> findByBin_BinValue(String binvalue);

}
