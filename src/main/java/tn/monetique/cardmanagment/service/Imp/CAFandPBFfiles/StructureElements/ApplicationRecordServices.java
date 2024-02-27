package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.StructureElements;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.Auth_User.AgentBank;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.Auth_User.MonetiqueAdmin;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;
import tn.monetique.cardmanagment.Entities.ConfigBank.*;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.CAFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.PBFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.Bank.BinRepository;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.AtmDataRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.EmvDataRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.POSPBFXDRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.PosDataRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.MonetiqueAdminRepo;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IAtmDataService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IEmvDataServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPOSPBFXDServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPosDataService;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationRecordServices implements IApplicationRecordServices {

    @Autowired
    IAtmDataService iAtmDataService;
    @Autowired
    IPosDataService iPosDataService;
    @Autowired
    IEmvDataServices iEmvDataServices;
    @Autowired
    IPOSPBFXDServices ipospbfxdServices;
    @Autowired
    CAFApplicationDataRecordRepository cafApplicationDataRecordRepository;
    @Autowired
    PBFApplicationDataRecordRepository pbfApplicationDataRecordRepository;
    @Autowired
    private AtmDataRepository atmDataRepository;
    @Autowired
    private PosDataRepository posDataRepository;
    @Autowired
    private EmvDataRepository emvDataRepository;
    @Autowired
    private POSPBFXDRepository pospbfxdRepository;
    @Autowired
    AdminBankRepository adminBankRepository;
    @Autowired
    MonetiqueAdminRepo monetiqueAdminRepo;
    @Autowired
    AgentBankRepository agentBankRepository;

    @Autowired
    IEncryptDecryptservi iEncryptDecryptservi;
    @Autowired
    CardHolderRepository cardHolderRepository;
    @Autowired
    private BinRepository binRepository;

    @Override
    public CAFApplicationDataRecord createCafApplication(CardHolder cardHolder, String BankName) {
        String binValue = cardHolder.getBin();
        AtmData atmData = atmDataRepository.findByBin_BinValue(binValue).orElse(null);
        PosData posData = posDataRepository.findByBin_BinValue(binValue).orElse(null);
        EmvData emvData = emvDataRepository.findByBin_BinValue(binValue).orElse(null);
        CAFApplicationDataRecord cafApplicationDataRecord = new CAFApplicationDataRecord();
        cafApplicationDataRecord.setCafCardHolder(cardHolder);
        cafApplicationDataRecord.setAtmData(atmData);
        cafApplicationDataRecord.setPosData(posData);
        cafApplicationDataRecord.setEmvData(emvData);
        cafApplicationDataRecord.setCardStatus("1");
        cafApplicationDataRecord.setMbrNum("000");
        cafApplicationDataRecord.setCardType(cardHolder.getCardtype());

        cafApplicationDataRecord.setFiid(cardHolder.getBankIdCode().toString());
        //cafApplicationDataRecord.setTotalWithdrawalLimit("000000000800");
        //cafApplicationDataRecord.setOfflineWithdrawalLimit("000000000800");
        // cafApplicationDataRecord.setTotalCashAdvanceLimit("000000000800");
        //cafApplicationDataRecord.setOfflineCashAdvanceLimit("000000000800");
        // cafApplicationDataRecord.setAggregateLimit("000000000800");
        // cafApplicationDataRecord.setOfflineAggregateLimit("000000000800");

        String inputDate = cardHolder.getDate2();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMyy");
        try {
            Date parsedDate = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyMM");
            String outputDate = outputFormat.format(parsedDate);
            cafApplicationDataRecord.setCardExpDate(outputDate);
            System.out.println("Converted Date (YYMM): " + outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cafApplicationDataRecord.setAcctNum(cardHolder.getFirstAccount());
        cafApplicationDataRecord.setAcctTyp("01");
        cafApplicationDataRecord.setAcctCnt("01");
        cafApplicationDataRecord.setAcctStat("1");
        cafApplicationDataRecord.setAcctDescr("CHECK ACCT");
        cafApplicationDataRecord.setAcctLgth("0040");
        cafApplicationDataRecord.setPan(cardHolder.getCardholderNumber());
        CAFApplicationDataRecord savedApplicationDataRecord = cafApplicationDataRecordRepository.save(cafApplicationDataRecord);
        cardHolderRepository.save(cardHolder);
        return savedApplicationDataRecord;

    }

    @Override
    public List<CAFApplicationDataRecord> getCAFApplicationDataRecordsByIDS(List<Long> customerIds) {
        List<CAFApplicationDataRecord> cafApplicationDataRecords = new ArrayList<>();
        for (Long customerId : customerIds) {
            CAFApplicationDataRecord record = getCAfapplirecById(customerId);
            if (record != null) {
                cafApplicationDataRecords.add(record);
            }
        }
        return cafApplicationDataRecords;
    }
    @Override
    public List<CAFApplicationDataRecord> getallcafbyuser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        AgentBank agentBank = agentBankRepository.findByUsername(username).orElse(null);
        MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepo.findByUsername(username).orElse(null);
        if (adminBank != null) {
            String bankName = adminBank.getBank().getBankName();
            return getAllCafBank(bankName);
        } else if (agentBank != null) {
            Agence agence = agentBank.getAgence();
            String agencebankname = agence.getBank().getBankName();
            String agentbankname= agentBank.getBankAdmin().getBank().getBankName();
            if(agence!=null && agencebankname == agentbankname ){
                String branchcode = agence.getBranchCode();
                return getAllCafbygency(branchcode);
            }else {
                return null;
            }
        } else if (monetiqueAdmin != null) {

            return getAllCaf();
        } else {
            return null;
        }
    }


    @Override
    public List<CAFApplicationDataRecord> getAllCafbygency(String branchCode) {

        List<CAFApplicationDataRecord> cafs = cafApplicationDataRecordRepository.findByCafCardHolder_Branchcode(branchCode);
        for (CAFApplicationDataRecord caf : cafs) {
            caf.setPan(iEncryptDecryptservi.decrypt(caf.getPan()));
            caf.setAcctNum(iEncryptDecryptservi.decrypt(caf.getAcctNum()));
        }
        return cafs ;
    }
    @Override
    public List<CAFApplicationDataRecord> getAllCaf() {

        List<CAFApplicationDataRecord> cafs = cafApplicationDataRecordRepository.findAll();
        for (CAFApplicationDataRecord caf : cafs) {
            caf.setPan(iEncryptDecryptservi.decrypt(caf.getPan()));
            caf.setAcctNum(iEncryptDecryptservi.decrypt(caf.getAcctNum()));
        }
        return cafs ;
    }
    @Override
    public List<CAFApplicationDataRecord> getAllCafBank(String Bankname) {

        List<CAFApplicationDataRecord> cafs = cafApplicationDataRecordRepository.findByCafCardHolder_Bank_BankName(Bankname);

        for (CAFApplicationDataRecord caf : cafs) {
            caf.setPan(iEncryptDecryptservi.decrypt(caf.getPan()));
            caf.setAcctNum(iEncryptDecryptservi.decrypt(caf.getAcctNum()));
        }

        return cafs;
    }

    @Override
    public String generateCAFApplicationDataRecordsForCard(List<Long> cafids) {
        StringBuilder result = new StringBuilder();
        int counter = 3;

        for (Long cafid : cafids) {
            CAFApplicationDataRecord cafApplicationDataRecord=getCAfapplirecById(cafid);
            System.out.println("ok"+cafApplicationDataRecord);
            String cntValue = String.format("%09d", counter);
            StringBuilder cafRecord = new StringBuilder();
            cafRecord.append(formatField("0346", 4));
            cafRecord.append(formatField(cntValue, 9));
            cafRecord.append(formatField(iEncryptDecryptservi.decrypt(cafApplicationDataRecord.getPan()), 19));
            cafRecord.append(formatField(cafApplicationDataRecord.getMbrNum(), 3));
            cafRecord.append(formatField(cafApplicationDataRecord.getRecordType(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getCardType(), 2));
            cafRecord.append(formatField(cafApplicationDataRecord.getFiid(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getCardStatus(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getPinOfset(), 16));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getFirstUsedDate(), 6));
            cafRecord.append(formatField(cafApplicationDataRecord.getLastResetDate(), 6));
            cafRecord.append(formatField(cafApplicationDataRecord.getCardExpDate(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getCardEffectiveDate(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getUserField1(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getSecondCardExpirationDate(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getSecondCardEffectiveDate(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getSecondCardStatus(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getUserField2(), 35));
            cafRecord.append(formatField(cafApplicationDataRecord.getUserFieldACI(), 50));
            cafRecord.append(formatField(cafApplicationDataRecord.getUserFieldREGN(), 50));
            cafRecord.append(formatField(cafApplicationDataRecord.getUserFieldCUST(), 50));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getLgth(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getUseLimit(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getOfflineWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getTotalCashAdvanceLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getOfflineCashAdvanceLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getMaximumDepositCreditAmount(), 10));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getLastUsed(), 6));
            cafRecord.append(formatField(cafApplicationDataRecord.getAtmData().getIssuerTransactionProfile(), 16));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getSegxLgth(), 16));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getTotalPurchaseLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getOfflinePurchaseLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getTotalCashAdvanceLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getOfflineCashAdvanceLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getTotalWithdrawalLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getUseLimit(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getTotalRefundCreditLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getOfflineRefundCreditLimit(), 12));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getReasonCode(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getLastUsed(), 6));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getUserField2(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getPosData().getIssuerTransactionProfile(), 16));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getSegxLgth(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getAtcLimit(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getSendCardBlock(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getSendPutData(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getVelocityLimitsLowerConsecutiveLimit(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getUserField2(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getDataTag(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getSendPinUnblock(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getSendPinChange(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getPinSyncAct(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getAccessScriptMgmtSubSys(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getIssApplDataFmt(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getEmvData().getActionTableIndex(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctLgth(), 4));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctCnt(), 2));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctTyp(), 2));
            cafRecord.append(formatField(iEncryptDecryptservi.decrypt(cafApplicationDataRecord.getAcctNum()), 19));

            cafRecord.append(formatField(cafApplicationDataRecord.getAcctStat(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctDescr(), 10));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctCorp(), 1));
            cafRecord.append(formatField(cafApplicationDataRecord.getAcctQual(), 1));
            System.out.println(cafApplicationDataRecord.getAcctNum());

            int totalLength = cafRecord.length();

            if (totalLength < 660) {
                cafRecord.append(String.format("%-" + (660 - totalLength) + "s", ""));
                cafRecord.setCharAt(659, 'Z');
            } else if (totalLength > 660) {
                cafRecord.setLength(660);
                cafRecord.setCharAt(659, 'Z');
            } else {
                cafRecord.setCharAt(659, 'Z');
            }

            cafRecord.append("\n");
            result.append(cafRecord);
            counter++;
            cafApplicationDataRecord.setCFAgenerated(true);
            cafApplicationDataRecordRepository.save(cafApplicationDataRecord);

        }

        System.out.println("ok    "+result.toString());
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

    //////////////////////////////PBFservices///////////////////////////////
    @Override
    public List<PBFApplicationDataRecord> getPBFApplicationDataRecordsBypbfIDs(List<Long> customerIds) {
        List<PBFApplicationDataRecord> pbfApplicationDataRecords = new ArrayList<>();

        for (Long customerId : customerIds) {

            PBFApplicationDataRecord record = getPBFApplicationDataRecordById(customerId);
            if (record != null) {
                pbfApplicationDataRecords.add(record);
            }
        }

        return pbfApplicationDataRecords;
    }

    @Override
    public List<PBFApplicationDataRecord> getallpbfbyuser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        AgentBank agentBank = agentBankRepository.findByUsername(username).orElse(null);
        MonetiqueAdmin monetiqueAdmin = monetiqueAdminRepo.findByUsername(username).orElse(null);
        if (adminBank != null) {
            String bankName = adminBank.getBank().getBankName();
            return getAllpbfBank(bankName);
        } else if (agentBank != null) {
            Agence agence = agentBank.getAgence();
            String agencebankname = agence.getBank().getBankName();
            String agentbankname= agentBank.getBankAdmin().getBank().getBankName();
            if(agence!=null && agencebankname == agentbankname ){
                String branchcode = agence.getBranchCode();
                return getAllPbfbygency(branchcode);
            }else {
                return null;
            }
        } else if (monetiqueAdmin != null) {

            return getAllPBf();
        } else {
            return null;
        }
    }


    @Override
    public List<PBFApplicationDataRecord> getAllPbfbygency(String branchCode) {

        List<PBFApplicationDataRecord> pbfs = pbfApplicationDataRecordRepository.findByPbfCardHolder_Branchcode(branchCode);
        for (PBFApplicationDataRecord pbf : pbfs) {
            pbf.setNumAccount(iEncryptDecryptservi.decrypt(pbf.getNumAccount()));
            pbf.getPbfCardHolder().setCardholderNumber(iEncryptDecryptservi.
                    decrypt( pbf.getPbfCardHolder().getCardholderNumber()));
        }
        return pbfs ;
    }
    @Override
    public List<PBFApplicationDataRecord> getAllPBf() {

        List<PBFApplicationDataRecord> pbfs = pbfApplicationDataRecordRepository.findAll();
        for (PBFApplicationDataRecord pbf : pbfs) {
            pbf.setNumAccount(iEncryptDecryptservi.decrypt(pbf.getNumAccount()));
            pbf.getPbfCardHolder().setCardholderNumber(iEncryptDecryptservi.
                    decrypt( pbf.getPbfCardHolder().getCardholderNumber()));
        }
        return pbfs ;
    }

    @Override
    public List<PBFApplicationDataRecord> getAllpbfBank(String Bankname) {

        List<PBFApplicationDataRecord> pbfs = pbfApplicationDataRecordRepository.findByPbfCardHolder_Bank_BankName(Bankname);

        for (PBFApplicationDataRecord pbf : pbfs) {
            pbf.setNumAccount(iEncryptDecryptservi.decrypt(pbf.getNumAccount()));
            pbf.getPbfCardHolder().setCardholderNumber(iEncryptDecryptservi.
                    decrypt( pbf.getPbfCardHolder().getCardholderNumber()));
        }
        return pbfs ;
    }

    @Override
    public PBFApplicationDataRecord createPBFApplication(CardHolder cardHolder, String BankName) {
        String binValue = cardHolder.getBin();
        POSPBFXD pospbfxd = pospbfxdRepository.findByBin_BinValue(binValue).orElse(null);;
        PBFApplicationDataRecord pbfApplicationDataRecord = new PBFApplicationDataRecord();
        pbfApplicationDataRecord.setPbfCardHolder(cardHolder);
        pbfApplicationDataRecord.setPospbfxd(pospbfxd);
        pbfApplicationDataRecord.setCnt("000000001");
        pbfApplicationDataRecord.setPrikeyFiid(cardHolder.getBank().getBankIdCode().toString());
        pbfApplicationDataRecord.setNumAccount(cardHolder.getFirstAccount());
        pbfApplicationDataRecord.setTyp("01");
        pbfApplicationDataRecord.setAcctStat("1");
        pbfApplicationDataRecord.setRecTyp("C");
        pbfApplicationDataRecord.setAmtOnHld("000000000000000000");
        pbfApplicationDataRecord.setOvrdrftLmt("0000000000");
        pbfApplicationDataRecord.setLastDepAmt("000000000000000");
        pbfApplicationDataRecord.setLastDepDat("000000");
        pbfApplicationDataRecord.setLastWdlDat("000000");
        pbfApplicationDataRecord.setLastWdlAmt("000000000000000");
        pbfApplicationDataRecord.setCrncyCde(cardHolder.getCurrencycode());
        PBFApplicationDataRecord savedPBFApplicationDataRecord = pbfApplicationDataRecordRepository.save(pbfApplicationDataRecord);



        return savedPBFApplicationDataRecord;

    }
    @Override
    public String generatePBFApplicationDataRecordsForCard(List<Long> PbfIds) {
        StringBuilder result = new StringBuilder();
        int counter = 3;
        for (Long pbfid : PbfIds) {

            PBFApplicationDataRecord pbfApplicationDataRecord=getPBFApplicationDataRecordById(pbfid);System.out.println("ok"+pbfApplicationDataRecord);String cntValue = String.format("%09d", counter);
            StringBuilder pbfRecord = new StringBuilder();
            pbfRecord.append(pbfformatField("0300", 4));
            pbfRecord.append(pbfformatField(cntValue, 9));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getPrikeyFiid(), 4));
            pbfRecord.append(pbfformatField(iEncryptDecryptservi.decrypt(pbfApplicationDataRecord.getNumAccount()), 19));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getTyp(), 2));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getAcctStat(), 1));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getRecTyp(), 1));
            pbfRecord.append(pbfformatField(pbfformatFieldforbalance(pbfApplicationDataRecord.getAvailBal(), 18),18));
            pbfRecord.append(pbfformatField(pbfformatFieldforbalance(pbfApplicationDataRecord.getLedgBal(), 18),18));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getAmtOnHld(), 18));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getOvrdrftLmt(), 10));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getLastDepDat(), 06));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getLastDepAmt(), 15));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getLastWdlDat(), 06));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getLastWdlAmt(), 15));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getCrncyCde(), 3));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getUserFld1(), 1));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getUserFldAci(), 50));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getUserFldRegn(), 50));
            pbfRecord.append(pbfformatField(pbfApplicationDataRecord.getUserFldCust(), 50));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getSegxLgth(), 4));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getTtlFloat(),15));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getDaysDelinq(), 2));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getMonthsActive(), 2));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getCycle1(), 2));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getCycle2(), 2));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getCycle3(), 2));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getUnknown(), 12));
            pbfRecord.append(formatField(pbfApplicationDataRecord.getPospbfxd().getUserFld2(), 1));


            int totalLength = pbfRecord.length();

            if (totalLength < 660) {
                pbfRecord.append(String.format("%" + (660 - totalLength) + "s", ""));
                pbfRecord.setCharAt(659, 'Z');
            } else if (totalLength > 660) {
                pbfRecord.setLength(660);
                pbfRecord.setCharAt(659, 'Z');
            } else {
                pbfRecord.setCharAt(659, 'Z');
            }

            pbfRecord.append("\n");
            result.append(pbfRecord);
            counter++;
            pbfApplicationDataRecord.setPBFgenerated(true);
            pbfApplicationDataRecordRepository.save(pbfApplicationDataRecord);
        }
        System.out.println("ok    "+result.toString());
        return result.toString();

    }


    private String pbfformatField(String input, int length) {
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
    public String pbfformatFieldforbalance(Long balance, int length) {
        String strBalance = String.format("%0" + length + "d", Math.abs(balance));

        // Check if the balance is greater than 0
        if (balance > 0) {
            char lastDigit = strBalance.charAt(strBalance.length() - 1);

            switch (lastDigit) {
                case '0':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "{";
                    break;
                case '1':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "A";
                    break;
                case '2':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "B";
                    break;
                case '3':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "C";
                    break;
                case '4':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "D";
                    break;
                case '5':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "E";
                    break;
                case '6':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "F";
                    break;
                case '7':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "G";
                    break;
                case '8':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "H";
                    break;
                case '9':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "I";
                    break;
                default:
                    break;
            }
        } else {
            char lastDigit = strBalance.charAt(strBalance.length() - 1);

            switch (lastDigit) {
                case '0':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "}";
                    break;
                case '1':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "J";
                    break;
                case '2':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "K";
                    break;
                case '3':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "L";
                    break;
                case '4':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "M";
                    break;
                case '5':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "N";
                    break;
                case '6':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "O";
                    break;
                case '7':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "P";
                    break;
                case '8':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "Q";
                    break;
                case '9':
                    strBalance = strBalance.substring(0, strBalance.length() - 1) + "R";
                    break;
                default:
                    break;
            }
        }



        return strBalance;
    }

    @Override
    public PBFApplicationDataRecord getPBFApplicationDataRecordByCustomerId(Long customerId) {
        PBFApplicationDataRecord pbfApplicationDataRecord = pbfApplicationDataRecordRepository
                .findByPbfCardHolder_CustomerId(customerId);
        return pbfApplicationDataRecord;
    }
    @Override
    public CAFApplicationDataRecord getCAfapplirecByCustomerId(Long customerId) {
        CAFApplicationDataRecord cafApplicationDataRecord = cafApplicationDataRecordRepository
                .findByCafCardHolder_CustomerId(customerId);
        return cafApplicationDataRecord;
    }
    @Override
    public PBFApplicationDataRecord getPBFApplicationDataRecordById(Long pbfid) {
        PBFApplicationDataRecord pbfApplicationDataRecord = pbfApplicationDataRecordRepository
                .findById(pbfid).orElse(null);
        return pbfApplicationDataRecord;
    }
    @Override
    public CAFApplicationDataRecord getCAfapplirecById(Long Cafid) {
        CAFApplicationDataRecord cafApplicationDataRecord = cafApplicationDataRecordRepository
                .findById(Cafid).orElse(null);
        return cafApplicationDataRecord;
    }

    @Override
    public void deletePBF(Long customerId) {

        PBFApplicationDataRecord existPBFrecord = pbfApplicationDataRecordRepository.findByPbfCardHolder_CustomerId(customerId);
        if (existPBFrecord!=null) {
            pbfApplicationDataRecordRepository.delete(existPBFrecord);
        } else {
            throw new IllegalArgumentException("PBF application record not exist");
        }
    }
    @Override
    public void deleteCAF(Long customerId) {

        CAFApplicationDataRecord existCAfrecord = cafApplicationDataRecordRepository.findByCafCardHolder_CustomerId(customerId);
        if (existCAfrecord!=null) {
            cafApplicationDataRecordRepository.delete(existCAfrecord);
        } else {
            throw new IllegalArgumentException("PBF application record not exist");
        }
    }



    ///////////////////////updatepbfandcaf///////////////////////////////
    @Override
    public PBFApplicationDataRecord updatePBFrecord(Long Idpbf, PBFApplicationDataRecord newpbfApplicationDataRecord) {
        PBFApplicationDataRecord existingPbf = getPBFApplicationDataRecordById(Idpbf);
        if (existingPbf != null) {
            CardHolder cardHolder = existingPbf.getPbfCardHolder();
            String binvalue = cardHolder.getBin();
            Long maxbalance = binRepository.findBinByBinValue(binvalue).getMaxbalance();

            // Check if the new ledger balance and available balance are less than or equal to the max balance
            if (newpbfApplicationDataRecord.getLedgBal() <= maxbalance &&
                    newpbfApplicationDataRecord.getAvailBal() <= maxbalance) {
                existingPbf.setLedgBal(newpbfApplicationDataRecord.getLedgBal());
                existingPbf.setAvailBal(newpbfApplicationDataRecord.getAvailBal());
                existingPbf.setPBFgenerated(false);

                existingPbf.setNumAccount(iEncryptDecryptservi.encrypt(newpbfApplicationDataRecord.getNumAccount()));

                pbfApplicationDataRecordRepository.save(existingPbf);
            } else {

                throw new IllegalArgumentException("Ledger balance or available balance exceeds maximum allowed balance.");
            }
        }
        return existingPbf;
    }

    @Override
    public CAFApplicationDataRecord updateCAFrecord(Long IdPBF, CAFApplicationDataRecord newCafApplicationDataRecord) {
        CAFApplicationDataRecord existingcaf = getCAfapplirecById(IdPBF);
        if (existingcaf != null) {
            existingcaf.setCardStatus(newCafApplicationDataRecord.getCardStatus());
            existingcaf.setCFAgenerated(false);
            cafApplicationDataRecordRepository.save(existingcaf);
        }
        return existingcaf;
    }



    public PBFApplicationDataRecord resetPBFGenerated(Long id) {
        Optional<PBFApplicationDataRecord> pbfApplicationDataRecord = pbfApplicationDataRecordRepository.findById(id);

        if(pbfApplicationDataRecord.isPresent()) {
            PBFApplicationDataRecord PBF = pbfApplicationDataRecord.get();
            PBF.setPBFgenerated(false);
            pbfApplicationDataRecordRepository.save(PBF);
            return PBF;
        } else {
            throw new EntityNotFoundException("PBF Record with ID: " + id + " not found");
        }
    }
    public CAFApplicationDataRecord resetCAFGenerated(Long id) {
        Optional<CAFApplicationDataRecord> cafApplicationDataRecord = cafApplicationDataRecordRepository.findById(id);

        if(cafApplicationDataRecord.isPresent()) {
            CAFApplicationDataRecord PBF = cafApplicationDataRecord.get();
            PBF.setCFAgenerated(false);
            cafApplicationDataRecordRepository.save(PBF);
            return PBF;
        } else {
            throw new EntityNotFoundException("PBF Record with ID: " + id + " not found");
        }
    }

}