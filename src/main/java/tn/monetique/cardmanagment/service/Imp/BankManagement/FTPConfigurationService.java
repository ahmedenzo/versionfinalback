package tn.monetique.cardmanagment.service.Imp.BankManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;
import tn.monetique.cardmanagment.exception.BankNotFoundException;
import tn.monetique.cardmanagment.exception.FTPConfigNotFoundException;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.repository.Bank.FTPConfigurationRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IftpConfigurationService;

import java.util.Optional;

@Service
public class FTPConfigurationService implements IftpConfigurationService {

    @Autowired
    private FTPConfigurationRepository ftpConfigurationRepository;
    private BankRepository bankRepository;

    // Method to create or update FTP configuration
    @Override
    public BankFTPConfig createAndAssignFTPConfiguration(Long bankId, BankFTPConfig ftpConfig) {
        Optional<Bank> optionalBank = bankRepository.findById(bankId);

        if (optionalBank.isPresent()) {
            Bank bank = optionalBank.get();
            // Set the association between Bank and BankFTPConfig
            bank.setBankFTPConfig(ftpConfig);
            ftpConfig.setBank(bank);

            // Save the BankFTPConfig
            BankFTPConfig savedFTPConfig = ftpConfigurationRepository.save(ftpConfig);

            // Update the Bank entity to reflect the new association
            bankRepository.save(bank);

            return savedFTPConfig;
        } else {
            // Handle case where bank with the given ID is not found
            throw new BankNotFoundException("Bank with ID " + bankId + " not found");
        }
    }
    @Override
    public BankFTPConfig updateFTPConfiguration(Long ftpConfigId, BankFTPConfig updatedFTPConfig) {
        BankFTPConfig existingFTPConfig = ftpConfigurationRepository.findById(ftpConfigId)
                .orElseThrow(() -> new FTPConfigNotFoundException("FTP Configuration with ID " + ftpConfigId + " not found"));

        // Update the existing FTP configuration
        existingFTPConfig.setServer(updatedFTPConfig.getServer());
        existingFTPConfig.setPort(updatedFTPConfig.getPort());
        existingFTPConfig.setUsername(updatedFTPConfig.getUsername());
        existingFTPConfig.setPassword(updatedFTPConfig.getPassword());
        existingFTPConfig.setRemotePath(updatedFTPConfig.getRemotePath());

        // Save the updated FTP configuration
        return ftpConfigurationRepository.save(existingFTPConfig);
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
