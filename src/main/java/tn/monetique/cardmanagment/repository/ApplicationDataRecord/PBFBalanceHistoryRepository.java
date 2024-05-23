package tn.monetique.cardmanagment.repository.ApplicationDataRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFBalanceHistory;

import java.util.List;

public interface PBFBalanceHistoryRepository extends JpaRepository<PBFBalanceHistory, Long> {

    List<PBFBalanceHistory> findByPbfApplicationDataRecordId(Long RecordId );
}
