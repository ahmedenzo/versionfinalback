package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.StructureElements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Model.FileTrailerRecord;
import tn.monetique.cardmanagment.Model.OrganisationTrailerRecord;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.CAFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.PBFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.ITrailerFileService;

import java.util.List;


@Service
public class TrailerFileService implements ITrailerFileService {

    @Autowired
    private IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    private CAFApplicationDataRecordRepository cafApplicationDataRecordRepository;
    @Autowired
    private PBFApplicationDataRecordRepository pbfApplicationDataRecordRepository;

    public String generatetrailerRecordString(FileTrailerRecord trailerRecord) {
        StringBuilder trailerRecordString = new StringBuilder();

        // Append attributes to the recordString and pad with spaces as needed
        String counter = String.format("%" + 9 + "s", trailerRecord.getRecordCount()).replace(' ', '0');
        trailerRecordString.append(padString(counter, 9));
        trailerRecordString.append(padString(trailerRecord.getRecordType(), 2));
        String numrecord = String.format("%" + 9 + "s", trailerRecord.getNumberOfRecords()).replace(' ', '0');
        trailerRecordString.append(padString(numrecord, 9));
        trailerRecordString.append(trailerRecord.getNextFileIndicator());
        trailerRecordString.append(padString(trailerRecord.getUserField1(), 3));

        // Adjust the total length to 660 and set the last character to 'Z'
        String finalRecordString = adjustLengthAndSetLastChar(trailerRecordString.toString());

        return finalRecordString;
    }

    // Helper method to pad a string with spaces to a specific length
    private String padString(String input, int length) {
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
    public String createAndGenerateTrailerRecord(List<Long> customerIds, String fileType) {
        List<CAFApplicationDataRecord> cafApplicationDataRecords = iApplicationRecordServices.getCAFApplicationDataRecordsByIDS(customerIds);
        List<PBFApplicationDataRecord> pbfApplicationDataRecords = iApplicationRecordServices.getPBFApplicationDataRecordsBypbfIDs(customerIds);
        FileTrailerRecord fileTrailerRecord = new FileTrailerRecord();
        fileTrailerRecord.setRecordType("FT");
        fileTrailerRecord.setNextFileIndicator("0");
        if ("CAF".equalsIgnoreCase(fileType)) {
            String numrec = String.valueOf(cafApplicationDataRecords.size());
            String count = String.valueOf(cafApplicationDataRecords.size() + 4);
            fileTrailerRecord.setRecordCount(count);
            fileTrailerRecord.setNumberOfRecords(numrec);
        } else if ("PBF".equalsIgnoreCase(fileType)) {
            String numrec = String.valueOf(pbfApplicationDataRecords.size());
            String count = String.valueOf(pbfApplicationDataRecords.size() + 4);
            fileTrailerRecord.setRecordCount(count);
            fileTrailerRecord.setNumberOfRecords(numrec);
        }


        return generatetrailerRecordString(fileTrailerRecord);
    }


    public String generateorgtrailerRecordString(OrganisationTrailerRecord organisationTrailerRecord,String fileType) {
        StringBuilder orgtrailerRecordString = new StringBuilder();

        // Append attributes to the recordString and pad with spaces as needed
        String counter = String.format("%" + 9 + "s", organisationTrailerRecord.getRecordCounter()).replace(' ', '0');
        orgtrailerRecordString.append(orgpadString(counter, 9));
        orgtrailerRecordString.append(orgpadString(organisationTrailerRecord.getRecordType(), 2));

        if ("PBF".equalsIgnoreCase(fileType)) {
            orgtrailerRecordString.append(orgpadString(
                    iApplicationRecordServices.pbfformatFieldforbalance(organisationTrailerRecord.getAmount(), 18), 18));
        }else if ("CAF".equalsIgnoreCase(fileType)) {
            String stramount = String.format("%0" + 18 + "d",organisationTrailerRecord.getAmount());
            orgtrailerRecordString.append(orgpadString(stramount, 18));
        }
        String numrec = String.format("%" + 9 + "s", organisationTrailerRecord.getNumberOfRecords()).replace(' ', '0');
        orgtrailerRecordString.append(orgpadString(numrec, 9));


        // Adjust the total length to 660 and set the last character to 'Z'
        String finalRecordString = adjustLengthAndSetLastChar(orgtrailerRecordString.toString());

        return finalRecordString;
    }

    // Helper method to pad a string with spaces to a specific length
    private String orgpadString(String input, int length) {
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
    public String createAndGenerateorgTrailerRecord(List<Long> customerIds, String fileType) {
        List<CAFApplicationDataRecord> cafApplicationDataRecords = iApplicationRecordServices.getCAFApplicationDataRecordsByIDS(customerIds);
        List<PBFApplicationDataRecord> pbfApplicationDataRecords = iApplicationRecordServices.getPBFApplicationDataRecordsBypbfIDs(customerIds);
        System.out.println("Number of PBF Records: " + pbfApplicationDataRecords.size());
        OrganisationTrailerRecord organisationTrailerRecord = new OrganisationTrailerRecord();
        organisationTrailerRecord.setRecordType("BT");
        if ("CAF".equalsIgnoreCase(fileType)) {
            organisationTrailerRecord.setAmount(0L);
            String recordcount = String.valueOf(cafApplicationDataRecords.size() + 3);
            organisationTrailerRecord.setRecordCounter(recordcount);
            String numrec = String.valueOf(cafApplicationDataRecords.size());
            organisationTrailerRecord.setNumberOfRecords(numrec);
            System.out.println("i'm in caf");
        } else if ("PBF".equalsIgnoreCase(fileType)) {
            System.out.println("Number of PBF Records: " + pbfApplicationDataRecords.size());
            Long sumOfledgBalances = calculateSumOfLedgBalances(pbfApplicationDataRecords);
            String recordcount = String.valueOf(pbfApplicationDataRecords.size() + 3);
            organisationTrailerRecord.setRecordCounter(recordcount);
            String numrec = String.valueOf(pbfApplicationDataRecords.size());

            organisationTrailerRecord.setNumberOfRecords(numrec);
            organisationTrailerRecord.setAmount(sumOfledgBalances);
            System.out.println("somme= " + sumOfledgBalances);
            System.out.println("i'm in pbf");
        }

        return generateorgtrailerRecordString(organisationTrailerRecord,fileType);
    }

    private String adjustLengthAndSetLastChar(String recordString) {
        if (recordString.length() < 660) {
            // Pad with spaces to achieve the total length of 660
            recordString = String.format("%-660s", recordString);
        } else if (recordString.length() > 660) {
            // If the string is longer than 660, truncate it
            recordString = recordString.substring(0, 660);
        }

        // Set the last character to 'Z'
        recordString = recordString.substring(0, recordString.length() - 1) + "Z";

        return recordString;
    }

    private Long calculateSumOfLedgBalances(List<PBFApplicationDataRecord> pbfApplicationDataRecords) {
        long sumOfLedgBalances = 0;
        System.out.println("Number of PBF Records: " + pbfApplicationDataRecords.size());
        for (int i = 0; i < pbfApplicationDataRecords.size(); i++) {
            PBFApplicationDataRecord pbfApplicationDataRecord = pbfApplicationDataRecords.get(i);
            Long ledgbal = pbfApplicationDataRecord.getLedgBal();
            System.out.println("Record " + (i + 1) + " - ledgbal: " + ledgbal);

            if (ledgbal != null) {
                sumOfLedgBalances += ledgbal;
            }
        }

        System.out.println("Sum of LedgBalances: " + sumOfLedgBalances);
        return (sumOfLedgBalances);
    }

}

