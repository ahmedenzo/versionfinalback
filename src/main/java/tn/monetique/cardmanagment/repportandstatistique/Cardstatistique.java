package tn.monetique.cardmanagment.repportandstatistique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.Entities.Auth_User.BankAdmin;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Cardstatistique implements  IcardStat{

    @Autowired
    private CardHolderRepository cardHolderRepository;
    @Autowired
private AdminBankRepository adminBankRepository;



    @Override
    public Long getGeneratedCardsCountForDay(LocalDate date, Authentication authentication ) {
        Timestamp startTimestamp = Timestamp.valueOf(date.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();

        return cardHolderRepository.countByCreatedAtBetweenAndBank(startTimestamp, endTimestamp, bank);
    }
@Override
    public Long getGeneratedCardsCountForMonth(int year, int month, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));
        return cardHolderRepository.countByCreatedAtBetweenAndBank(startTimestamp, endTimestamp, bank);
    }
@Override
    public Map<String, Long> getGeneratedCardsCountByType(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();
        List<Object[]> results = cardHolderRepository.countByCardTypeAndBank(bank);
        Map<String, Long> cardTypeCounts = new HashMap<>();
        for (Object[] result : results) {
            cardTypeCounts.put((String) result[0], (Long) result[1]);
        }
        return cardTypeCounts;
    }
@Override
    public Map<String, Long> getGeneratedCardsCountByBranch(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        BankAdmin adminBank = adminBankRepository.findByUsername(username).orElse(null);
        Bank bank = adminBank.getBank();
        List<Object[]> results = cardHolderRepository.countByBranchcodeAndBank(bank);
        Map<String, Long> branchCounts = new HashMap<>();
        for (Object[] result : results) {
            branchCounts.put((String) result[0], (Long) result[1]);
        }
        return branchCounts;
    }
}