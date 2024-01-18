package tn.monetique.cardmanagment.repository.Configbankentitiesrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;

import java.util.Optional;
@Repository
public interface PosDataRepository extends JpaRepository<PosData,Long> {
    Optional<PosData> findById(Long posDataId);
    Optional<PosData> findByBin_BinId(Long binId);
    Optional<PosData> findByBin_BinValue(String binvalue);

}
