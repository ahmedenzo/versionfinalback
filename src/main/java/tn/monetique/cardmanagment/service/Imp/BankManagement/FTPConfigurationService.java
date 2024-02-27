package tn.monetique.cardmanagment.service.Imp.BankManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;
import tn.monetique.cardmanagment.repository.Bank.FTPConfigurationRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IftpConfigurationService;

@Service
public class FTPConfigurationService implements IftpConfigurationService {

    @Autowired
    private FTPConfigurationRepository ftpConfigurationRepository;

    // Method to create or update FTP configuration
    @Override
    public BankFTPConfig saveFTPConfiguration(BankFTPConfig ftpConfiguration) {
        return ftpConfigurationRepository.save(ftpConfiguration);
    }

    // Method to retrieve FTP configuration by bank
    @Override
    public BankFTPConfig getFTPConfigurationByBank(Bank bank) {
        return ftpConfigurationRepository.findByBank(bank);
    }

    // Method to delete FTP configuration
    @Override
    public void deleteFTPConfiguration(BankFTPConfig ftpConfiguration) {
        ftpConfigurationRepository.delete(ftpConfiguration);
    }
    @Override
    public BankFTPConfig getFTPConfigurationById(Long id) {
        return ftpConfigurationRepository.findById(id).orElse(null);
    }
}
