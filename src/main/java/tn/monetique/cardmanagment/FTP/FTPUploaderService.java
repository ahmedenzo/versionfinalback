package tn.monetique.cardmanagment.FTP;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;
import tn.monetique.cardmanagment.Entities.DataInputCard.UploadedFile;
import tn.monetique.cardmanagment.repository.Bank.FTPConfigurationRepository;
import tn.monetique.cardmanagment.repository.DataInputCard.UploadedFileRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.Card.IGeneratePortFile;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FTPUploaderService implements IFTPUploader {
    @Autowired
    UploadedFileRepository uploadedFileRepository;
    @Autowired
    UserService userService;
    @Autowired
    IGeneratePortFile iGeneratePortFile;
    @Autowired
    AdminBankRepository adminBankRepository;
    @Autowired
    FTPConfigurationRepository ftpConfigurationRepository;
    private final FTPClient ftpClient;
    public FTPUploaderService() {
        this.ftpClient = new FTPClient();
    }

    @Override
    public boolean connect(BankFTPConfig ftpConfiguration) throws IOException {
        ftpClient.connect(ftpConfiguration.getServer(), ftpConfiguration.getPort());
        boolean success = ftpClient.login(ftpConfiguration.getUsername(), ftpConfiguration.getPassword());
        // Optionally, you can check if the connection and login were successful
        if (success) {
            System.out.println("Connected to FTP server with the expected credentials.");
        } else {
            System.out.println("Failed to connect to FTP server with the provided credentials.");
        }
        return success;
    }
    @Override
    public boolean uploadFiles(List<Long> fileInformationIds, Authentication authentication) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        // Retrieve user's bank information
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();

        try {
            BankFTPConfig ftpConfiguration = ftpConfigurationRepository.findByBank(bank);
            if (ftpConfiguration == null) {
                System.out.println("FTP configuration not found for the user's bank.");
                return false;
            }
            ftpClient.connect(ftpConfiguration.getServer(), ftpConfiguration.getPort());
            boolean success = ftpClient.login(ftpConfiguration.getUsername(), ftpConfiguration.getPassword());

            ftpClient.changeWorkingDirectory(ftpConfiguration.getRemotePath());

            // Set file transfer mode to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            boolean allFilesUploaded = true;

            for (Long fileInformationId : fileInformationIds) {
                GeneratedFileInformation fileInformation = iGeneratePortFile.getfilebyid(fileInformationId);
                if (fileInformation != null) {
                    String fileName = fileInformation.getFileName();
                    String filePath = fileInformation.getFilePath();
                    FileInputStream fileInputStream = new FileInputStream(filePath);

                    // Upload the file
                    boolean uploaded = ftpClient.storeFile(fileName, fileInputStream);
                    if (uploaded) {
                        System.out.println("File " + fileName + " uploaded successfully.");
                        UploadedFile uploadedFile = new UploadedFile();
                        uploadedFile.setFileName(fileName);
                        uploadedFile.setUploadedBy(username);
                        uploadedFile.setUpoaded(true);
                        fileInformation.setSent(true);
                        uploadedFileRepository.save(uploadedFile);
                    } else {
                        System.out.println("Failed to upload file " + fileName);
                        allFilesUploaded = false;
                    }
                }
            }

            // Disconnect from the FTP server
            disconnect();

            return allFilesUploaded;
        } catch (IOException e) {
            System.out.println("Error uploading files: " + e.getMessage());
            return false;
        }
    }


    @Override
    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
    @Override
    public List<UploadedFile> getallFiles() {
        // Assuming you have a repository for accessing the CardHolder entities
        List<UploadedFile> uploadedFiles = uploadedFileRepository.findAll();

        return uploadedFiles;
    }
}

