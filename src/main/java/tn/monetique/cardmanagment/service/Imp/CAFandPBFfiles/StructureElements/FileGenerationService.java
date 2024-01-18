package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.StructureElements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IFileGenerationService;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IHeaderFilesServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.ITrailerFileService;
import tn.monetique.cardmanagment.service.Interface.Card.IGeneratePortFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class FileGenerationService implements IFileGenerationService {
    @Autowired
    IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    IHeaderFilesServices iHeaderFilesServices;
    @Autowired
    ITrailerFileService iTrailerFileService;
    @Autowired
    IGeneratePortFile iGeneratePortFile;
    @Autowired
    AdminBankRepository adminBankRepository;


    @Override
    public ResponseEntity<FileSystemResource> generateCafFile(List<Long> customerIds, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        String BankIdCode = adminBank.getBank().getBankIdCode().toString();
        String bankname= adminBank.getBank().getBankName();
        try {

            String fileHeader = iHeaderFilesServices.createAndGenerateheaderRecord( "CAF");
            String organisationHeader = iHeaderFilesServices.createAndGenerateorganisationRecord();
            String CAFapplicationDataRecords = iApplicationRecordServices.generateCAFApplicationDataRecordsForCard(customerIds);
            String organisationTrailer = iTrailerFileService.createAndGenerateorgTrailerRecord(customerIds, "CAF");
            String fileTrailer = iTrailerFileService.createAndGenerateTrailerRecord(customerIds, "CAF");


            StringBuilder cafFileContent = new StringBuilder();
            cafFileContent.append(fileHeader).append("\n");
            cafFileContent.append(organisationHeader).append("\n");
            cafFileContent.append(CAFapplicationDataRecords);
            cafFileContent.append(organisationTrailer).append("\n");
            cafFileContent.append(fileTrailer).append("\n");

            String timestamp = new SimpleDateFormat("yyyyMMdd_HH").format(new Date());
            String fileName = bankname+"_CAF_" + timestamp + ".txt"; // Specify the desired file name
            // Obtain the application's working directory
            String workingDirectory = System.getProperty("user.dir");

            // Create a subdirectory based on the timestamp
            String subdirectoryName = "CAF-files";
            String directoryPath = workingDirectory + File.separator + subdirectoryName;

            // Create the full file path
            String filePath = directoryPath + File.separator + fileName;

            // Create the subdirectory if it doesn't exist
            File subdirectory = new File(directoryPath);
            if (!subdirectory.exists()) {
                subdirectory.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(cafFileContent.toString());
            }
            FileSystemResource fileResource = new FileSystemResource(filePath);

            // Prepare the response with appropriate headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filetype= "CAF";
            GeneratedFileInformation generatedfile = iGeneratePortFile.saveGeneratedFileInformation(fileName, filePath,username,bankname,filetype);
            // Return the file as a ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileResource.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(fileResource);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error and return appropriate ResponseEntity if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    @Override
    public ResponseEntity<FileSystemResource> generatePBFFile(List<Long> PbfIds, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        String BankIdCode = adminBank.getBank().getBankIdCode().toString();
        String bankname= adminBank.getBank().getBankName();
        try {

            String fileHeader = iHeaderFilesServices.createAndGenerateheaderRecord( "PBF");

            String organisationHeader = iHeaderFilesServices.createAndGenerateorganisationRecord();

            String PBFapplicationDataRecords = iApplicationRecordServices.generatePBFApplicationDataRecordsForCard(PbfIds);

            String organisationTrailer = iTrailerFileService.createAndGenerateorgTrailerRecord(PbfIds, "PBF");

            String fileTrailer = iTrailerFileService.createAndGenerateTrailerRecord(PbfIds, "PBF");




            StringBuilder PBFFileContent = new StringBuilder();
            PBFFileContent.append(fileHeader).append("\n");
            PBFFileContent.append(organisationHeader).append("\n");
            PBFFileContent.append(PBFapplicationDataRecords);
            PBFFileContent.append(organisationTrailer).append("\n");
            PBFFileContent.append(fileTrailer).append("\n");

            String timestamp = new SimpleDateFormat("yyyyMMdd_HH").format(new Date());
            String fileName = bankname+"PBF_"+BankIdCode + timestamp + ".txt"; // Specify the desired file name
            // Obtain the application's working directory
            String workingDirectory = System.getProperty("user.dir");


            // Create a subdirectory based on the timestamp
            String subdirectoryName = "PBF-files";
            String directoryPath = workingDirectory + File.separator + subdirectoryName;

            // Create the full file path
            String filePath = directoryPath + File.separator + fileName;

            // Create the subdirectory if it doesn't exist
            File subdirectory = new File(directoryPath);
            if (!subdirectory.exists()) {
                if (subdirectory.mkdirs()) {
                    System.out.println("Directory created successfully.");
                } else {
                    System.out.println("Failed to create the directory.");
                    // Handle the case where the directory creation fails
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(PBFFileContent.toString());
            }
            FileSystemResource fileResource = new FileSystemResource(filePath);

            // Prepare the response with appropriate headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filetype= "PBF";
            GeneratedFileInformation generatedfile = iGeneratePortFile.saveGeneratedFileInformation(fileName, filePath,username,bankname,filetype);
            // Return the file as a ResponseEntity

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileResource.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(fileResource);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error and return appropriate ResponseEntity if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}