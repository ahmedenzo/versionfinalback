package tn.monetique.cardmanagment.repository.Bank;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;

public interface FTPConfigurationRepository extends JpaRepository<BankFTPConfig, Long> {
    BankFTPConfig findByBank(Bank bank);
}

