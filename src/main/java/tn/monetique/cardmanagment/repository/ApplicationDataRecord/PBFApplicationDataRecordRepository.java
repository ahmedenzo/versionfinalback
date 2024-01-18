package tn.monetique.cardmanagment.repository.ApplicationDataRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;

import java.util.List;

@Repository
public interface PBFApplicationDataRecordRepository extends JpaRepository<PBFApplicationDataRecord, Long> {
    PBFApplicationDataRecord findByPbfCardHolder_CustomerId(Long customer);

    List<PBFApplicationDataRecord> findByPbfCardHolder_Bank_BankName(String bankName);
    List<PBFApplicationDataRecord>findByPbfCardHolder_Branchcode(String branchcode);
}
