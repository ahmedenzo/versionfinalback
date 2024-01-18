package tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.StructureElements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Model.FileHeaderRecord;
import tn.monetique.cardmanagment.Model.OrganisationHeaderRecord;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.CAFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.repository.ApplicationDataRecord.PBFApplicationDataRecordRepository;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IHeaderFilesServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class HeaderFilesServices  implements IHeaderFilesServices {

    @Autowired
    private CAFApplicationDataRecordRepository cafApplicationDataRecordRepository;
    @Autowired
    private PBFApplicationDataRecordRepository pbfApplicationDataRecordRepository;
    @Autowired
    private IApplicationRecordServices iApplicationRecordServices;

    public String generatehederRecordString(FileHeaderRecord headerRecord ) {
        StringBuilder recordString = new StringBuilder();

        // Append attributes to the recordString and pad with spaces as needed
        recordString.append(headerpadString(headerRecord.getRecordCount(), 9));
        recordString.append(headerpadString(headerRecord.getRecordType(), 2));
        recordString.append(headerpadString(headerRecord.getRefreshType(), 1));
        recordString.append(headerpadString(headerRecord.getApplicationType(), 2));
        recordString.append(headerpadString(headerRecord.getGrpcode(), 4));
        recordString.append(headerpadString(headerRecord.getTapeDate(), 8));
        recordString.append(headerpadString(headerRecord.getTapeTime(), 4));
        recordString.append(headerpadString(headerRecord.getLn(), 4));
        recordString.append(headerpadString(headerRecord.getReleaseNumber(), 2));
        recordString.append(headerpadString(headerRecord.getPartitionNumber(), 2));
        recordString.append(headerpadString(headerRecord.getAtmLastExtractDate(), 6));
        recordString.append(headerpadString(headerRecord.getAtmImpactingStartDate(), 8));
        recordString.append(headerpadString(headerRecord.getAtmImpactingStartTime(), 12));
        recordString.append(headerpadString(headerRecord.getPosLastExtractDate(), 6));
        recordString.append(headerpadString(headerRecord.getPosImpactingStartDate(), 8));
        recordString.append(headerpadString(headerRecord.getPosImpactingStartTime(), 12));
        recordString.append(headerpadString(headerRecord.getTlrLastExtractDate(), 6));
        recordString.append(headerpadString(headerRecord.getTlrImpactingStartDate(), 8));
        recordString.append(headerpadString(headerRecord.getTlrImpactingStartTime(), 12));
        recordString.append(headerpadString(headerRecord.getImpactType(), 1));
        recordString.append(headerpadString(headerRecord.getCafExponent(), 1));
        recordString.append(headerpadString(headerRecord.getPreAuthSupport(), 1));
        recordString.append(headerpadString(headerRecord.getUserField2(), 5));
        recordString.append(headerpadString(headerRecord.getTbLastExtractDate(), 6));
        recordString.append(headerpadString(headerRecord.getTbImpactingStartDate(), 8));
        recordString.append(headerpadString(headerRecord.getTbImpactingStartTime(), 12));
        int totalLength = recordString.length();
        if (totalLength < 660) {
            recordString.append(String.format("%-" + (660 - totalLength) + "s", ""));
        } else if (totalLength > 660) {
            recordString.setLength(660);
        }
        recordString.setCharAt(659, 'Z');
        return recordString.toString();
    }

    // Helper method to pad a string with spaces to a specific length
    private String headerpadString(String input, int length) {
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
    public String createAndGenerateheaderRecord(String fileType) {
        FileHeaderRecord headerRecord = new FileHeaderRecord();
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyMMdd");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("HHmm");
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("HHmmssSS");

        String formattedDate1 = date.format(formatter1);
        String formattedDate2 = date.format(formatter2);
        String formattedTime3 = time.format(formatter3);
        String formattedTime4 = time.format(formatter4);
        headerRecord.setRecordCount("000000001");
        headerRecord.setRecordType("FH");
        headerRecord.setRefreshType("1");
        headerRecord.setTapeDate( formattedDate1 );
        headerRecord.setTapeTime(formattedTime3);
        headerRecord.setLn("PRO2");
        headerRecord.setReleaseNumber("60");
        ////////////ATM/////////////
        headerRecord.setAtmLastExtractDate(formattedDate2);
        headerRecord.setAtmImpactingStartDate(formattedDate1);
        headerRecord.setAtmImpactingStartTime(formattedTime4+"0000");
        /////////////////Pos///////////////////
        headerRecord.setPosLastExtractDate(formattedDate2);
        headerRecord.setPosImpactingStartDate(formattedDate1);
        headerRecord.setPosImpactingStartTime(formattedTime4+"0000");
        ///////////////TLR///////////
        headerRecord.setTlrLastExtractDate("");
        headerRecord.setTlrImpactingStartDate("");
        headerRecord.setTlrImpactingStartTime("");
        headerRecord.setImpactType("1");
        headerRecord.setCafExponent("0");
        headerRecord.setPreAuthSupport("0");
        headerRecord.setUserField2("");

        if ("CAF".equalsIgnoreCase(fileType)) {
            headerRecord.setApplicationType("CF");
        } else if ("PBF".equalsIgnoreCase(fileType)) {
            headerRecord.setApplicationType("PF");
        }

        return generatehederRecordString(headerRecord);
    }
    public String generateorganisationRecordString(OrganisationHeaderRecord organisationHeaderRecord) {
        StringBuilder organisationrecordString = new StringBuilder();

        // Append attributes to the recordString and pad with spaces as needed
        organisationrecordString.append(OrgpadString(organisationHeaderRecord.getRecordCounter(), 9));
        organisationrecordString.append(OrgpadString(organisationHeaderRecord.getRecordType(), 2));
        organisationrecordString.append(OrgpadString(organisationHeaderRecord.getCardIssuer(), 4));
        organisationrecordString.append(OrgpadString(organisationHeaderRecord.getEndRange(), 28));
        organisationrecordString.append(OrgpadString(organisationHeaderRecord.getUserField(), 1));

        // Ensure the final string has a total length of 44 characters
        int totalLength = organisationrecordString.length();
        if (totalLength < 660) {
            organisationrecordString.append(String.format("%-" + (660 - totalLength) + "s", ""));
        } else if (totalLength > 660) {
            organisationrecordString.setLength(660);
        }

        // Set the last character to "Z"
        organisationrecordString.setCharAt(659, 'Z');

        return organisationrecordString.toString();
    }
    @Override
    public String createAndGenerateorganisationRecord() {
        OrganisationHeaderRecord organisationHeaderRecord = new OrganisationHeaderRecord();
        organisationHeaderRecord.setRecordCounter("000000002");
        organisationHeaderRecord.setRecordType("BH");
        organisationHeaderRecord.setCardIssuer("0151");
        return generateorganisationRecordString(organisationHeaderRecord);

    }

    // Helper method to pad a string with spaces to a specific length
    private String OrgpadString(String input, int length) {
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
}