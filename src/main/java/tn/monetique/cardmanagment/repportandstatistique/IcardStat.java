package tn.monetique.cardmanagment.repportandstatistique;

import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IcardStat {


    List<CardHolder> getCardHoldersByDateIntervalAndBank(LocalDate startDate, LocalDate endDate, Authentication authentication);


    abstract byte[] generatePdf(List<CardHolder> cardHolders, LocalDate startDate, LocalDate endDate) throws IOException;

    /////////////////////////////////////////////////////////////////
    List<PBFApplicationDataRecord> getPBFApplicationDataRecordsByDateIntervalAndBank(LocalDate startDate, LocalDate endDate, Authentication authentication);

    byte[] generatePdfpbf(List<PBFApplicationDataRecord> pbfApplicationDataRecords, LocalDate startDate, LocalDate endDate) throws IOException;
}
