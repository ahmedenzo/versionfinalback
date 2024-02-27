package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;

public interface IftpConfigurationService {
    // Method to create or update FTP configuration
    BankFTPConfig saveFTPConfiguration(BankFTPConfig ftpConfiguration);

    // Method to retrieve FTP configuration by bank
    BankFTPConfig getFTPConfigurationByBank(Bank bank);

    // Method to delete FTP configuration
    void deleteFTPConfiguration(BankFTPConfig ftpConfiguration);

    BankFTPConfig getFTPConfigurationById(Long id);
}
