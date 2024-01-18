package tn.monetique.cardmanagment.repository.Bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;


import java.util.List;
@Repository
public interface AgencyRepository extends JpaRepository<Agence, Long>{

    List<Agence> findAgenceByBank_BankName(String  bankName);
    List<Agence> findAgencesByBank_BankId(Long bankid);
    Agence findByAgenceName(String AgenceName);
}
