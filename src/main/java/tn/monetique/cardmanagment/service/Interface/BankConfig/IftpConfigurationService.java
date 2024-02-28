package tn.monetique.cardmanagment.service.Interface.BankConfig;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;

public interface IftpConfigurationService {


    // Method to create or update FTP configuration
    BankFTPConfig createAndAssignFTPConfiguration(Long bankId, BankFTPConfig ftpConfig);

    BankFTPConfig updateFTPConfiguration(Long ftpConfigId, BankFTPConfig updatedFTPConfig);

    // Method to retrieve FTP configuration by bank
    BankFTPConfig getFTPConfigurationByBank(Bank bank);

    // Method to delete FTP configuration
    void deleteFTPConfiguration(BankFTPConfig ftpConfiguration);

    BankFTPConfig getFTPConfigurationById(Long id);
}
