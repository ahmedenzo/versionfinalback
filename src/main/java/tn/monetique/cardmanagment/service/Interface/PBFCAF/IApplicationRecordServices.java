package tn.monetique.cardmanagment.service.Interface.PBFCAF;

import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;

import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;

import java.util.List;


public interface IApplicationRecordServices {






    //CAFApplicationDataRecord createCafApplication(CardHolder cardHolder ,String atmDataId, String emvDataId, String posDataId);


    CAFApplicationDataRecord createCafApplication(CardHolder cardHolder, String BankName);


    List<CAFApplicationDataRecord> getCAFApplicationDataRecordsByIDS(List<Long> customerIds);

    List<CAFApplicationDataRecord> getallcafbyuser(Authentication authentication);

    List<CAFApplicationDataRecord> getAllCafbygency(String branchCode);

    List<CAFApplicationDataRecord> getAllCaf();

    List<CAFApplicationDataRecord> getAllCafBank(String Bankname);

    String generateCAFApplicationDataRecordsForCard(List<Long> customerIds);

    //////////////////////////////PBFservices///////////////////////////////

   // PBFApplicationDataRecord createPBFApplication(CardHolder cardHolder, String posPbfXdId, PBFApplicationDataRecord inputpbfApplicationDataRecord);

    //////////////////////////////PBFservices///////////////////////////////
    List<PBFApplicationDataRecord> getPBFApplicationDataRecordsBypbfIDs(List<Long> customerIds);

    List<PBFApplicationDataRecord> getallpbfbyuser(Authentication authentication);

    List<PBFApplicationDataRecord> getAllPbfbygency(String branchCode);

    List<PBFApplicationDataRecord> getAllPBf();

    List<PBFApplicationDataRecord> getAllpbfBank(String Bankname);

    PBFApplicationDataRecord createPBFApplication(CardHolder cardHolder, String BankName);

    String generatePBFApplicationDataRecordsForCard(List<Long> customerIds);


    String pbfformatFieldforbalance(Long balance, int length);

    PBFApplicationDataRecord getPBFApplicationDataRecordByCustomerId(Long customerId);

    CAFApplicationDataRecord getCAfapplirecByCustomerId(Long customerId);


    PBFApplicationDataRecord updatePBFrecord(Long customerId, PBFApplicationDataRecord newpbfApplicationDataRecord);

    PBFApplicationDataRecord getPBFApplicationDataRecordById(Long pbfid);

    CAFApplicationDataRecord getCAfapplirecById(Long Cafid);

    void deletePBF(Long customerId);

    void deleteCAF(Long customerId);

    CAFApplicationDataRecord updateCAFrecord(Long customerId, CAFApplicationDataRecord newCafApplicationDataRecord);

    //String generateCAFApplicationDataRecordsForCard(List<CAFApplicationDataRecord> cafApplicationDataRecords);
}
