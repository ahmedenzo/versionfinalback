package tn.monetique.cardmanagment.repository.Configbankentitiesrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;

import java.util.Optional;
@Repository
public interface AtmDataRepository extends JpaRepository<AtmData,Long> {
    Optional<AtmData> findById(Long atmDataId);

    Optional<AtmData> findByBin_BinId(Long binId);

    Optional<AtmData> findByBin_BinValue(String binvalue);
}
