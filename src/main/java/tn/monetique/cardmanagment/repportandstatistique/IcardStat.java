package tn.monetique.cardmanagment.repportandstatistique;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Map;

public interface IcardStat {
    Long getGeneratedCardsCountForDay(LocalDate date, Authentication authentication);

    Long getGeneratedCardsCountForMonth(int year, int month, Authentication authentication);

    Map<String, Long> getGeneratedCardsCountByType(Authentication authentication);

    Map<String, Long> getGeneratedCardsCountByBranch(Authentication authentication);
}
