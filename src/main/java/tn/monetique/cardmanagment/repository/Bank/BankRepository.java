package tn.monetique.cardmanagment.repository.Bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByBankName(String bankName);

}
