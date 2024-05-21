package tn.monetique.cardmanagment.FTP;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FTPUploaderService implements IFTPUploader {

    private static final Logger logger = LoggerFactory.getLogger(FTPUploaderService.class);

    private final UploadedFileRepository uploadedFileRepository;
    private final IGeneratePortFile iGeneratePortFile;
    private final AdminBankRepository adminBankRepository;
    private final FTPConfigurationRepository ftpConfigurationRepository;

    private Session session;
    private ChannelSftp sftpChannel;

    @Autowired
    public FTPUploaderService(UploadedFileRepository uploadedFileRepository, IGeneratePortFile iGeneratePortFile,
                              AdminBankRepository adminBankRepository, FTPConfigurationRepository ftpConfigurationRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.iGeneratePortFile = iGeneratePortFile;
        this.adminBankRepository = adminBankRepository;
        this.ftpConfigurationRepository = ftpConfigurationRepository;
    }

    @Override
    public boolean connect(BankFTPConfig ftpConfiguration) {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(ftpConfiguration.getUsername(), ftpConfiguration.getServer(), ftpConfiguration.getPort());
            session.setPassword(ftpConfiguration.getPassword());
            session.setConfig("StrictHostKeyChecking", "yes");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            return sftpChannel.isConnected();
        } catch (JSchException e) {
            logger.error("Failed to connect to SFTP server: {}", e.getMessage());
            return false;
        }
    }



    @Override
    public boolean uploadFiles(List<Long> fileInformationIds, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();

        try {
            BankFTPConfig ftpConfiguration = ftpConfigurationRepository.findByBank(bank);
            if (ftpConfiguration == null) {
                logger.error("FTP configuration not found for the user's bank.");
                return false;
            }
            if (!connect(ftpConfiguration)) {
                logger.error("Failed to connect to SFTP server.");
                return false;
            }

            boolean allFilesUploaded = true;

            for (Long fileInformationId : fileInformationIds) {
                GeneratedFileInformation fileInformation = iGeneratePortFile.getfilebyid(fileInformationId);
                if (fileInformation != null) {
                    String fileName = fileInformation.getFileName();
                    String filePath = fileInformation.getFilePath();

                    String remotePath;

                    if (fileInformation.getFileType().equalsIgnoreCase("Card input data file")) {
                        remotePath = ftpConfiguration.getRemotePathPorter();
                    } else {
                        remotePath = ftpConfiguration.getRemotePqthCAFPBF();
                    }

                    try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                        sftpChannel.cd(remotePath);
                        sftpChannel.put(fileInputStream, fileName);
                        logger.info("File {} uploaded successfully to remote path: {}", fileName, remotePath);
                        UploadedFile uploadedFile = new UploadedFile();
                        uploadedFile.setFileName(fileName);
                        uploadedFile.setUploadedBy(username);
                        uploadedFile.setUpoaded(true);
                        fileInformation.setSent(true);
                        uploadedFileRepository.save(uploadedFile);
                    } catch (IOException | SftpException e) {
                        logger.error("Failed to upload file {}: {}", fileName, e.getMessage());
                        allFilesUploaded = false;
                    }
                }
            }
            disconnect();
            return allFilesUploaded;
        } catch (IOException e) {
            logger.error("Error uploading files: {}", e.getMessage());
            return false;
        }
    }



    @Override
    public void disconnect() throws IOException {
        if (sftpChannel != null && sftpChannel.isConnected()) {
            sftpChannel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    @Override
    public List<UploadedFile> getallFiles() {
        // Assuming you have a repository for accessing the CardHolder entities
        List<UploadedFile> uploadedFiles = uploadedFileRepository.findAll();

        return uploadedFiles;
    }
}

