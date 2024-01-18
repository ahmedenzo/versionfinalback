package tn.monetique.cardmanagment.service.Imp.CardHolderServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.exception.CardAlreadyGeneratedException;
import tn.monetique.cardmanagment.exception.CardNotConfirmedException;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.DataInputCard.GeneratedFileInformationRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;
import tn.monetique.cardmanagment.service.Interface.Card.IGeneratePortFile;
import tn.monetique.cardmanagment.service.Interface.Card.IcardHolderService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
@Service
public class GeneratePortFile implements IGeneratePortFile {

    @Autowired
    private GeneratedFileInformationRepository generatedFileInformationRepository;
    @Autowired
    private IcardHolderService icardHolderService;
    @Autowired
    private IEncryptDecryptservi iEncryptDecryptservi;
    @Autowired
    IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    private AdminBankRepository adminBankRepository;
    @Autowired
    private CardHolderRepository cardHolderRepository;
    @Autowired
    MonetiqueAdminRepo monetiqueAdminRepo;

    @Override
    public String generateStructuredatainputforCards(List<Long> customerIds) {

        List<CardHolder> cards = icardHolderService.getCardsbyCustomerIds(customerIds);
        System.out.println(cards);
        StringBuilder result = new StringBuilder();
        StringBuilder error = new StringBuilder();
        for (CardHolder card : cards)
            if (card.isConfirmation() && !card.isCardgenerated()) {
                StringBuilder dataIput = new StringBuilder();
                dataIput.append(formatField(card.getHeaderRecord(), 4));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getCardholderNumber()), 19));
                dataIput.append(formatField(card.getUpdatecode(), 1));
                dataIput.append(formatField(card.getCardtype(), 2));
                dataIput.append(formatField(card.getName(), 26));
                dataIput.append( formatField("", 26));
                dataIput.append(formatField(card.getCorporateName(), 26));
                dataIput.append(formatField(card.getAddress(), 32));
                dataIput.append(formatField(card.getAddresstwo(), 32));
                dataIput.append(formatField(card.getAddressthree(), 32));
                dataIput.append(formatField(card.getPostalCode(), 9));
                dataIput.append( formatField("", 26));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getFirstAccount()), 24));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getSecondAccount()), 24));
                dataIput.append(formatField(card.getBranchcode(), 5));
                dataIput.append(formatField(card.getDate1(), 4));
                dataIput.append(formatField(card.getDate2(), 4));
                dataIput.append(formatField(card.getCardProcessIndicator(), 1));
                dataIput.append( formatField("", 29));
                dataIput.append(formatField(card.getPinoffset(), 12));
                dataIput.append(formatField(card.getFreesCode(), 3));
                dataIput.append(formatField(card.getTerritorycode(), 1));
                dataIput.append( formatField("00", 2));
                dataIput.append(formatField(card.getJulianDate(), 6));
                dataIput.append(formatField(card.getBankIdCode().toString(), 5));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String formattedBirthDate = card.getBirthDate().format(formatter);
                dataIput.append(formatField(formattedBirthDate, 8));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getPassportId()),10));
                dataIput.append( formatField("", 6));
                dataIput.append(formatField(card.getCountryCode(), 3));
                dataIput.append(formatField(card.getCityCode(), 5));
                dataIput.append(formatField(card.getRenewOption(), 1));
                dataIput.append( formatField("", 26));
                dataIput.append(formatField(card.getSourcecode(), 1));
                dataIput.append(formatField(card.getPrimarycardcode(), 1));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getCardholderNumber()), 19));
                dataIput.append( formatField("", 5));
                dataIput.append(formatField(card.getCurrencycode(), 3));
                dataIput.append(formatField(card.getOperatorUserCode(), 6));
                dataIput.append(formatField(card.getCustomerId().toString(), 24));
                dataIput.append( formatField("", 6));
                dataIput.append(formatField(card.getPkiindicator(), 1));
                dataIput.append(formatField(card.getAcs(), 1));
                dataIput.append(formatField(iEncryptDecryptservi.decrypt(card.getCin()), 16));
                dataIput.append( formatField("", 29));
                dataIput.append(formatField(card.getCountryPhonecode(), 3));
                dataIput.append(formatField(card.getPhoneNumber(), 8));
                dataIput.append(formatField(card.getEmail(), 40));
                dataIput.append( formatField("", 3));
                dataIput.append("\n");
                result.append(dataIput);
                // Set the cardgenerated attribute to true
                card.setCardgenerated(true);
                String bankname= card.getBank().getBankName();
                CAFApplicationDataRecord cafApplicationDataRecord=iApplicationRecordServices.getCAfapplirecByCustomerId(card.getCustomerId());
                PBFApplicationDataRecord pbfApplicationDataRecord=iApplicationRecordServices.getPBFApplicationDataRecordByCustomerId(card.getCustomerId());
                if(cafApplicationDataRecord==null) {
                    CAFApplicationDataRecord newcaf = iApplicationRecordServices.createCafApplication(
                            card, bankname);}
                if(pbfApplicationDataRecord==null){
                    PBFApplicationDataRecord newpbf= iApplicationRecordServices.createPBFApplication(card,
                            bankname);}
                cardHolderRepository.save(card);
            }else if (!card.isConfirmation()) {
                error.append("Card with card number "
                        + iEncryptDecryptservi.decrypt(card.getCardholderNumber()) + " is not confirmed.");
                throw new CardNotConfirmedException(error.toString());
            } else if (card.isCardgenerated()) {
                error.append("Card with card number " +
                        iEncryptDecryptservi.decrypt(card.getCardholderNumber()) + " is already generated.");
                throw new CardAlreadyGeneratedException(error.toString());
            }

        return result.toString();
    }


    private String formatField(String input, int length) {
        if (input == null) {
            input = "";
        }

        if (input.length() > length) {
            // If the input is longer than the desired length, truncate it
            return input.substring(0, length);
        } else if (input.length() < length) {
            // If the input is shorter than the desired length, pad it with spaces
            return String.format("%-" + length + "s", input);
        } else {
            // If the input is already the desired length, return it as is
            return input;
        }

    }

    @Override
    public ResponseEntity<FileSystemResource> generateDatainputforcards(List<Long> customerIds, Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        String BankIdCode = adminBank.getBank().getBankIdCode().toString();
        String bankname= adminBank.getBank().getBankName();
        try {
            String timestamp = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
            String fileName = "PORT"+BankIdCode+"-" + timestamp +".txt"; // Specify the desired file name
            // Obtain the application's working directory
            String workingDirectory = System.getProperty("user.dir");

            // Create a subdirectory based on the timestamp
            String subdirectoryName = "PORT-files";
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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(generateStructuredatainputforCards(customerIds) );
            }


            FileSystemResource fileResource = new FileSystemResource(filePath);

            // Prepare the response with appropriate headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filetype= "Card input data file";
            GeneratedFileInformation generatedfile = saveGeneratedFileInformation(fileName, filePath,username,bankname,filetype);
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
    public GeneratedFileInformation saveGeneratedFileInformation(String fileName, String filePath, String username, String bankname,String filetype) {
        GeneratedFileInformation fileInfo = generatedFileInformationRepository.findByFileName(fileName);
        if (fileInfo==null){
            GeneratedFileInformation newfileInfo = new GeneratedFileInformation();
            newfileInfo.setFilePath(filePath);
            newfileInfo.setFileName(fileName);
            newfileInfo.setGeneratedBy(username);
            newfileInfo.setFileType(filetype);
            newfileInfo.setBankName(bankname);
            generatedFileInformationRepository.save(newfileInfo);
            return newfileInfo;
        }else {
            fileInfo.setFilePath(filePath);
            fileInfo.setGeneratedBy(username);
            generatedFileInformationRepository.save(fileInfo);
            return fileInfo;
        }

    }
    @Override
    public List<GeneratedFileInformation> getallfilesbyuser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepo.findByUsername(username).orElse(null);
        if (adminBank != null) {
            String bankName = adminBank.getBank().getBankName();
            return getAllFilesbyBank(bankName);
        } else if (monetiqueAdmin != null) {
            return getAllfiles();
        } else {
            return null;
        }
    }




    public List<GeneratedFileInformation> getAllfiles() {

        List<GeneratedFileInformation> files = generatedFileInformationRepository.findAll();

        return files ;
    }

    public List<GeneratedFileInformation> getAllFilesbyBank(String Bankname) {

        List<GeneratedFileInformation> files = generatedFileInformationRepository.findByBankName(Bankname);

        return files;
    }
    @Override
    public ResponseEntity<FileSystemResource> downloadGeneratedFile(String fileName) {
        GeneratedFileInformation fileInfo = generatedFileInformationRepository.findByFileName(fileName);

        if (fileInfo != null) {
            File file = new File(fileInfo.getFilePath());

            if (file.exists()) {
                try {
                    FileSystemResource fileResource = new FileSystemResource(file);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getFileName());
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(fileResource.contentLength())
                            .contentType(MediaType.parseMediaType("application/octet-stream"))
                            .body(fileResource);
                } catch (IOException e) {
                    // Handle the exception, log an error, and return an appropriate response.
                    e.printStackTrace(); // or log.error(e.getMessage(), e)
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }

        // Return an appropriate response if the file doesn't exist or there's an error.
        return ResponseEntity.notFound().build();
    }
    @Override
    public GeneratedFileInformation getfilebyid(Long fileId) {
        GeneratedFileInformation generatedFileInformation = generatedFileInformationRepository
                .findById(fileId).orElse(null);
        return generatedFileInformation;
    }
}
