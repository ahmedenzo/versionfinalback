package tn.monetique.cardmanagment.service.Imp.CardHolderServices;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;

import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.CAFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.PBFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;
import tn.monetique.cardmanagment.service.Interface.Card.IcardHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.UserService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CardHolderService implements IcardHolderService {
    @Autowired
    CardHolderRepository cardHolderRepository;
    @Autowired
    IEncryptDecryptservi iEncryptDecryptservi;
    @Autowired
    UserService userService;
    @Autowired
    IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private CAFApplicationDataRecordRepository cafApplicationDataRecordRepository;
    @Autowired
    PBFApplicationDataRecordRepository pbfApplicationDataRecordRepository;
    @Autowired
    private AgentBankRepository  agentBankRepository;
    @Autowired
    private BinRepository binRepository;
    @Autowired
    private AdminBankRepository adminBankRepository;
    @Autowired
    private MonetiqueAdminRepo monetiqueAdminRepository;


    // private static final String ENCRYPTION_KEY = "0123456789ABCDEF0123456789ABCDEF";
//Cration du carte
    @Override
    public CardHolder DataINPutpreparation(CardHolder cardHolder, String bankname, Bin Selectedbin, Agence Useragence, String username) {

        Bank bank = bankRepository.findByBankName(bankname).orElse(null);
        cardHolder.setBank(bank);
        String cardNumber = generateUniqueCardNumber(Selectedbin.getBinValue());
        cardHolder.setCurrencycode(Selectedbin.getCurrency());
        cardHolder.setBin(Selectedbin.getBinValue());
        cardHolder.setBranchcode(Useragence.getBranchCode());
        cardHolder.setCountryCode(bank.getCountryCode());
        cardHolder.setCountryPhonecode(cardHolder.getCountryPhonecode());
        long timestampMillis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
        String formattedDate = date.format(formatter);
        cardHolder.setDate1(formattedDate);

// Calculate date2 based on the expireRange years
        LocalDate date2 = date.plusYears(Selectedbin.getExpireRange());
        String formattedDate2 = date2.format(formatter);
        cardHolder.setDate2(formattedDate2);
        cardHolder.setBankIdCode(bank.getBankIdCode());
        cardHolder.setPrimarycardcode("0");
        cardHolder.setBirthDate(cardHolder.getBirthDate());
        cardHolder.setCorporateName(cardHolder.getCorporateName());
        cardHolder.setCardProcessIndicator(cardHolder.getCardProcessIndicator());
        cardHolder.setEmail(cardHolder.getEmail());
        cardHolder.setPostalCode(Useragence.getCityCode());
        cardHolder.setPhoneNumber(cardHolder.getPhoneNumber());
        cardHolder.setName(cardHolder.getName());
        cardHolder.setRenewOption(cardHolder.getRenewOption());
        cardHolder.setCardProcessIndicator(cardHolder.getCardProcessIndicator());
        cardHolder.setUpdatecode("1");
        cardHolder.setHeaderRecord("PO00");
        cardHolder.setCardProcessIndicator("D");
        cardHolder.setTerritorycode(Selectedbin.getTerritorycode());
        cardHolder.setCardtype(Selectedbin.getCodeType());
        cardHolder.setFreesCode("001");
        Timestamp currentDate = new Timestamp(System.currentTimeMillis());
        cardHolder.setJulianDate(convertToJulianDate(currentDate));
        cardHolder.setCardholderNumber(iEncryptDecryptservi.encrypt(cardNumber));
        System.out.println("bank.getBankIdCode"+ bank.getBankIdCode());
        System.out.println("bank.getBankIdCode"+ bank);
        if("0151".equals(bank.getBankIdCode())){
        cardHolder.setFirstAccount(cardHolder.getCardholderNumber());
            System.out.println("d5al if");
        }
        else{
            cardHolder.setFirstAccount(iEncryptDecryptservi.encrypt(cardHolder.getFirstAccount()));
            System.out.println("d5al elese");
        }

        if (cardHolder.getSecondAccount()!=null){
            cardHolder.setSecondAccount(iEncryptDecryptservi.decrypt(cardHolder.getSecondAccount()));
        }
        cardHolder.setPassportId(cardHolder.getPassportId());
        cardHolder.setCityCode(Useragence.getCityCode());
        cardHolder.setCin(cardHolder.getCin());
        cardHolder.setCreatedBy(username);
        cardHolderRepository.save(cardHolder);
        return cardHolder;
    }
    @Override
    public CardHolder createNewCard(CardHolder cardHolder, Long SelectedbinId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username =userDetails.getUsername();
        AgentBank agentBank = agentBankRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Fake user"));
        Agence agencyofuser = agentBank.getAgence();

        String bankname = agencyofuser.getBank().getBankName();
        Bin Selectedbin = binRepository.findById(SelectedbinId).orElse(null);
        CardHolder createdCardHolder = DataINPutpreparation(cardHolder,bankname,Selectedbin,agencyofuser,username);
        return createdCardHolder;
    }
    @Override
    public CardHolder updategeneratedcard(Long customerId, Long selectedBinId, CardHolder updatedData, Authentication authentication) {
        CardHolder existingCardholder = getCardHolderById(customerId);
        Bin selectedBin = binRepository.findById(selectedBinId).orElse(null);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        if (existingCardholder != null) {
            if (existingCardholder.isCardgenerated()) {
                System.out.println("cardgenerated");
                // Only update the fields that need to be updated
                if ("2".equals(updatedData.getUpdatecode())) {
                    existingCardholder.setPassportId(iEncryptDecryptservi.encrypt(updatedData.getPassportId()));
                    existingCardholder.setAddress(updatedData.getAddress());
                    existingCardholder.setBirthDate(updatedData.getBirthDate());
                    existingCardholder.setCorporateName(updatedData.getCorporateName());
                    existingCardholder.setFirstAccount(iEncryptDecryptservi
                            .encrypt(existingCardholder.getFirstAccount()));
                    existingCardholder.setEmail(updatedData.getEmail());
                    existingCardholder.setName(updatedData.getName());
                    existingCardholder.setPhoneNumber(updatedData.getPhoneNumber());
                    existingCardholder.setCin(updatedData.getCin());
                    existingCardholder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    existingCardholder.setUpdatedBy(username);
                    existingCardholder.setUpdatecode(updatedData.getUpdatecode());
                    existingCardholder.setCardgenerated(false);
                    existingCardholder.setCardProcessIndicator(updatedData.getCardProcessIndicator());
                    existingCardholder.setCardholderNumber(iEncryptDecryptservi
                            .encrypt(existingCardholder.getCardholderNumber()));
                    existingCardholder.setStatuscard(updatedData.getStatuscard());
                    cardHolderRepository.save(existingCardholder);

                    /////for caf
                    CAFApplicationDataRecord cafcard=cafApplicationDataRecordRepository.findByCafCardHolder_CustomerId(customerId);
                    cafcard.setCFAgenerated(false);
                    cafcard.setAcctNum(updatedData.getFirstAccount());
                    cafApplicationDataRecordRepository.save(cafcard);
                    /////for pbf
                    PBFApplicationDataRecord PBFrecord =pbfApplicationDataRecordRepository.findByPbfCardHolder_CustomerId(customerId);
                    PBFrecord.setPBFgenerated(false);
                    PBFrecord.setNumAccount(updatedData.getFirstAccount());
                    pbfApplicationDataRecordRepository.save(PBFrecord);

                    System.out.println("updatecard");
                    return existingCardholder;

                } else if ("4".equals(updatedData.getUpdatecode()))  {


                    long timestampMillis = System.currentTimeMillis();
                    Instant instant = Instant.ofEpochMilli(timestampMillis);
                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
                    String formattedDate = date.format(formatter);
                    existingCardholder.setDate1(formattedDate);
                    // Calculate date2 based on the expireRange years
                    LocalDate date2 = date.plusYears(selectedBin.getExpireRange());
                    String formattedDate2 = date2.format(formatter);
                    existingCardholder.setDate2(formattedDate2);
                    existingCardholder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    existingCardholder.setUpdatedBy(username);
                    existingCardholder.setUpdatecode(updatedData.getUpdatecode());
                    existingCardholder.setCardgenerated(false);
                    existingCardholder.setCardProcessIndicator(" ");


                    existingCardholder.setCardholderNumber(iEncryptDecryptservi
                            .encrypt(existingCardholder.getCardholderNumber()));
                    existingCardholder.setFirstAccount(iEncryptDecryptservi
                            .encrypt(existingCardholder.getFirstAccount()));
                    existingCardholder.setPassportId(existingCardholder.getPassportId());
                    existingCardholder.setCin(existingCardholder.getCin());
                    existingCardholder.setStatuscard(updatedData.getStatuscard());
                    /////////  forcaf
                    CAFApplicationDataRecord cafcard=cafApplicationDataRecordRepository.findByCafCardHolder_CustomerId(customerId);
                    cafcard.setCardExpDate(formattedDate2);
                    cafcard.setCFAgenerated(false);
                    cafApplicationDataRecordRepository.save(cafcard);
                    /////////
                    cardHolderRepository.save(existingCardholder);
                    System.out.println("renew");
                    return existingCardholder;
                     // You can choose to return a message or null here
               }else if ("3".equals(updatedData.getUpdatecode())) {
                    existingCardholder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    existingCardholder.setUpdatedBy(username);
                    existingCardholder.setUpdatecode(updatedData.getUpdatecode());
                    existingCardholder.setCardgenerated(false);
                    existingCardholder.setCardProcessIndicator("N");
                    existingCardholder.setCardholderNumber(iEncryptDecryptservi
                            .encrypt(existingCardholder.getCardholderNumber()));
                    existingCardholder.setFirstAccount(iEncryptDecryptservi
                            .encrypt(existingCardholder.getFirstAccount()));
                    existingCardholder.setPassportId(existingCardholder.getPassportId());
                    existingCardholder.setCin(existingCardholder.getCin());
                    existingCardholder.setStatuscard(updatedData.getStatuscard());
                    /////forcaf///////
                    CAFApplicationDataRecord cafcard=cafApplicationDataRecordRepository.findByCafCardHolder_CustomerId(customerId);
                    cafcard.setCardStatus("9");
                    cafcard.setCFAgenerated(false);
                    cafApplicationDataRecordRepository.save(cafcard);
                    cardHolderRepository.save(existingCardholder);
                    System.out.println("cancelation");
                    return existingCardholder;}
            }
            return null;
    }else {

            return null;
        }
    }

    @Override
    public CardHolder UpdateDataInput(Long customerId, Long selectedBinId, CardHolder updatedData, Authentication authentication) {
        CardHolder existingCardholder = cardHolderRepository.findById(customerId).orElse(null);
        Bin selectedBin = binRepository.findById(selectedBinId).orElse(null);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        if (existingCardholder != null) {
            if (!existingCardholder.isConfirmation()) {

                System.out.println("hamza222");
                // Only update the fields that need to be updated
                if (!existingCardholder.getBin().equals(selectedBin.getBinValue())) {
                    String cardNumber = generateUniqueCardNumber(selectedBin.getBinValue());
                    existingCardholder.setCardholderNumber(iEncryptDecryptservi.encrypt(cardNumber));
                    existingCardholder.setFirstAccount(iEncryptDecryptservi.encrypt(cardNumber));
                    existingCardholder.setBin(selectedBin.getBinValue());
                    existingCardholder.setCurrencycode(selectedBin.getCurrency());
                    existingCardholder.setCardtype(selectedBin.getCardType());

                    long timestampMillis = System.currentTimeMillis();
                    Instant instant = Instant.ofEpochMilli(timestampMillis);
                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
                    String formattedDate = date.format(formatter);
                    existingCardholder.setDate1(formattedDate);

// Calculate date2 based on the expireRange years
                    LocalDate date2 = date.plusYears(selectedBin.getExpireRange());
                    String formattedDate2 = date2.format(formatter);
                    existingCardholder.setDate2(formattedDate2);
                    existingCardholder.setDate2(formattedDate2);
                }

                if (updatedData.getPassportId() != null) {
                    existingCardholder.setPassportId(updatedData.getPassportId());
                }
                existingCardholder.setAddress(updatedData.getAddress());
                existingCardholder.setBirthDate(updatedData.getBirthDate());
                existingCardholder.setCorporateName(updatedData.getCorporateName());
                existingCardholder.setEmail(updatedData.getEmail());
                existingCardholder.setName(updatedData.getName());
                existingCardholder.setPhoneNumber(updatedData.getPhoneNumber());

                if (updatedData.getCin() != null) {
                    existingCardholder.setCin(updatedData.getCin());
                }

                existingCardholder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                existingCardholder.setUpdatedBy(username);

            }
            cardHolderRepository.save(existingCardholder);
            System.out.println("hamza3333");
            return existingCardholder;
        } else {
            // Handle the case where existingCardholder is null
            return null; // You can choose to return a message or null here
        }
    }
    @Override
    public List<CardHolder> getAllCardHolders(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        AgentBank agentBank = agentBankRepository.findByUsername(username).orElse(null);
        MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepository.findByUsername(username).orElse(null);
        if (adminBank != null) {
            String bankName = adminBank.getBank().getBankName();
            return getAllCardHolderbyBank(bankName);
        } else if (agentBank != null) {
            Agence agence = agentBank.getAgence();
            String agencebankname = agence.getBank().getBankName();
            String agentbankname= agentBank.getBankAdmin().getBank().getBankName();
            if(agence!=null && agencebankname == agentbankname ){
                String branchcode = agence.getBranchCode();
            return getAllCardHolderbyagency(branchcode);
            }else {
                return null;
            }
        } else if (monetiqueAdmin != null) {
            List<CardHolder> cardHolders = cardHolderRepository.findAll();
            for (CardHolder cardHolder : cardHolders) {
                cardHolder.setCardholderNumber(iEncryptDecryptservi.decrypt(cardHolder.getCardholderNumber()));
                cardHolder.setFirstAccount(iEncryptDecryptservi.decrypt(cardHolder.getFirstAccount()));
                if (cardHolder.getSecondAccount()!=null){
                    cardHolder.setSecondAccount(iEncryptDecryptservi.decrypt(cardHolder.getSecondAccount()));
                }

            }
            return cardHolders;
        } else {
            return null;
        }
    }


    @Override
    public List<CardHolder> getAllCardHolderbyagency(String branchCode) {

        List<CardHolder> cardHolders = cardHolderRepository.findByBranchcode(branchCode);
        System.out.println(cardHolders);

        for (CardHolder cardHolder : cardHolders) {
            cardHolder.setCardholderNumber(iEncryptDecryptservi.decrypt(cardHolder.getCardholderNumber()));
            cardHolder.setFirstAccount(iEncryptDecryptservi.decrypt(cardHolder.getFirstAccount()));
            if (cardHolder.getSecondAccount()!=null){
                cardHolder.setSecondAccount(iEncryptDecryptservi.decrypt(cardHolder.getSecondAccount()));
            }
            cardHolder.setPassportId(cardHolder.getPassportId());
            cardHolder.setCin(cardHolder.getCin());
        }
        return cardHolders;
    }
    @Override
    public List<CardHolder> getAllCardHolderbyBank(String Bankname) {

            List<CardHolder> cardHolders = cardHolderRepository.findByBank_BankName(Bankname);

            for (CardHolder cardHolder : cardHolders) {
                cardHolder.setCardholderNumber(iEncryptDecryptservi.decrypt(cardHolder.getCardholderNumber()));
                cardHolder.setFirstAccount(iEncryptDecryptservi.decrypt(cardHolder.getFirstAccount()));
                if (cardHolder.getSecondAccount()!=null){
                    cardHolder.setSecondAccount(iEncryptDecryptservi.decrypt(cardHolder.getSecondAccount()));
                }
                cardHolder.setPassportId(cardHolder.getPassportId());
                cardHolder.setCin(cardHolder.getCin());
            }

            return cardHolders;
        }


    @Override
    public void deleteDataInput(Long customerId) {

        Optional<CardHolder> optionalCardHolder = cardHolderRepository.findById(customerId);
        if (optionalCardHolder.isPresent()) {
            CardHolder cardHolder = optionalCardHolder.get();
            if (!cardHolder.isConfirmation()) {
                cardHolderRepository.deleteById(customerId);
            } else {
                throw new IllegalArgumentException("Cannot delete confirmed card holder.");
            }
        } else {
            throw new NoSuchElementException("Card holder not found.");
        }
    }

    @Override
    public CardHolder getCardHolderById(Long customerId) {
        CardHolder cardHolder = cardHolderRepository.findById(customerId).orElse(null);
        if (cardHolder != null) {
            // Decrypt the required fields
            cardHolder.setCardholderNumber(iEncryptDecryptservi.decrypt(cardHolder.getCardholderNumber()));
            cardHolder.setFirstAccount(iEncryptDecryptservi.decrypt(cardHolder.getFirstAccount()));
            if (cardHolder.getSecondAccount()!=null){
            cardHolder.setSecondAccount(iEncryptDecryptservi.decrypt(cardHolder.getSecondAccount()));
            }
            cardHolder.setPassportId(cardHolder.getPassportId());
            cardHolder.setCin(cardHolder.getCin());
            // Decrypt other fields as needed

            return cardHolder;
        }
        return null;
    }

    @Override
    public CardHolder Confirmation(Long customerId) {
        CardHolder cardHolder = cardHolderRepository.findById(customerId).orElse(null);
        if (cardHolder != null && !cardHolder.isConfirmation()) {
            cardHolder.skipUpdateTimestamp();
            boolean newConfirmationValue = !cardHolder.isConfirmation(); // Toggle the value
            cardHolder.setConfirmation(newConfirmationValue);
            cardHolderRepository.save(cardHolder);
        }
        return cardHolder;
    }


    ////////////////////////////////////Generation de num de card ///////////////////////////////////////////////////

    public String generateUniqueCardNumber(String bin) {
        String cardNumber;
        String Fullcardnumber;
        boolean isUnique = false;

        do {
            cardNumber = bin + generateCardNumber();
            Fullcardnumber= cardNumber + generateCheckDigitusingLuhn(cardNumber);
            boolean exists = cardHolderRepository.existsByCardholderNumber(Fullcardnumber);
            if (!exists) {
                isUnique = true;
            }
        } while (!isUnique);

        return Fullcardnumber;
    }


    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumberBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10);
            cardNumberBuilder.append(digit);
        }
        return cardNumberBuilder.toString();
    }



    public static int generateCheckDigitusingLuhn(String cardNumber) {
        int lg = cardNumber.length() + 1;
        int i = 1, X = 0, Y = 0;
        int checkDigit;
        while (i < lg) {
            X = Character.getNumericValue(cardNumber.charAt(lg - i - 1));
            X = X * ((i % 2) + 1);
            Y = (X % 10) + (X / 10) + Y;
            i = i + 1;
        }
        if ((Y % 10) == 0)
        {
            checkDigit= 0;
            return checkDigit;
        } else {
            checkDigit= (10 * ((Y / 10) + 1)) - Y;
            return checkDigit;
        }
    }

    ///////////////////////////Convert to juliandate//////////////////////////////
    private String convertToJulianDate(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        int year = calendar.get(Calendar.YEAR) - 1900;
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        // Calculate Julian date
        int julianDate = 1000 * year + dayOfYear;

        // Format it as a string if needed
        String formattedJulianDate = String.format("%d", julianDate);
        return formattedJulianDate;
    }

    @Override
    public List<CardHolder> getCardsbyCustomerIds(List<Long> customerIds) {
        List<CardHolder> cards = new ArrayList<>();

        for (Long customerId : customerIds) {

            CardHolder card = cardHolderRepository.findById(customerId).orElse(null);
            if (card != null) {
                cards.add(card);
            }
        }

        return cards;
    }


    ////////////////////Optional services ////////////////////////////
   /* @Override
    public List<CardHolder> getCardHoldersByDay(LocalDate fixedDay) {
        // Assuming you have a method in the service layer to retrieve all cardholders
        List<CardHolder> allCardHolders = getAllCardHolders();

        // Filter the cardholders based on the fixed day
        List<CardHolder> cardHoldersForFixedDay = allCardHolders.stream()
                .filter(cardHolder -> cardHolder.getCreatedAt().toLocalDateTime().toLocalDate().equals(fixedDay))
                .collect(Collectors.toList());
        return cardHoldersForFixedDay;
    }*/
    @Override
    public CardHolder resetCardGenerated(Long customerId) {
        Optional<CardHolder> cardHolderOptional = cardHolderRepository.findById(customerId);

        if(cardHolderOptional.isPresent()) {
            CardHolder cardHolder = cardHolderOptional.get();
            cardHolder.skipUpdateTimestamp();  // Ensure updatedAt isn't modified.
            cardHolder.setCardgenerated(false);
            cardHolderRepository.save(cardHolder);
            return cardHolder;
        } else {
            throw new EntityNotFoundException("CardHolder with ID: " + customerId + " not found");
        }
    }

    @Override
    public CardHolder checkgeneratedfile(Long customerId) {
        Optional<CardHolder> cardHolderOptional = cardHolderRepository.findById(customerId);

        if (cardHolderOptional.isPresent()) {
            CardHolder cardHolder = cardHolderOptional.get();

            if (!cardHolder.isCardgenerated()) {
                cardHolder.skipUpdateTimestamp(); // Set skipUpdateTimestamp to true before making changes
                cardHolder.setCardgenerated(true);
                cardHolderRepository.save(cardHolder);
            }
            return cardHolder;
        } else {
            throw new EntityNotFoundException("CardHolder with ID: " + customerId + " not found");
        }
    }





}








