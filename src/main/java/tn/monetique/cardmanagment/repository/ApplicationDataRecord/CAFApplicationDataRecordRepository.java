package tn.monetique.cardmanagment.repository.ApplicationDataRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;

import java.util.List;

@Repository
public interface CAFApplicationDataRecordRepository extends JpaRepository<CAFApplicationDataRecord, Long> {
    CAFApplicationDataRecord findByCafCardHolder_CustomerId(Long customerId);
    List<CAFApplicationDataRecord> findByCafCardHolder_Bank_BankName(String bankName);

    List<CAFApplicationDataRecord> findByCafCardHolder_Branchcode(String branchcode);

}
