package tn.monetique.cardmanagment.repository.Configbankentitiesrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;

import java.util.Optional;
@Repository
public interface EmvDataRepository extends JpaRepository<EmvData,Long> {
    Optional<EmvData> findById(Long emvDataId);
    Optional<EmvData> findByBin_BinId(Long binId);
    Optional<EmvData> findByBin_BinValue(String binvalue);
}
