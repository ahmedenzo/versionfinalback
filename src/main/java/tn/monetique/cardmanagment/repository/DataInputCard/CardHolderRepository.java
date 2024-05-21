package tn.monetique.cardmanagment.repository.DataInputCard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {


    Optional<CardHolder> findById( Long CustomerId);
    List<CardHolder>findByBranchcode(String brancheCode);


    List<CardHolder> findAll();
    boolean existsByCardholderNumber(String CardholderNumber);

    CardHolder findByPassportId(String passport);

    List<CardHolder>findByBank_BankName(String bankname);


    List<CardHolder> findByCreatedAtAfterAndBank(Timestamp createdAt, Bank bank);

    List<CardHolder> findByCreatedAtBetweenAndBank(Timestamp startDate, Timestamp endDate, Bank bank);

    @Query("SELECT COUNT(c) FROM CardHolder c WHERE c.createdAt BETWEEN :startDate AND :endDate AND c.bank = :bank")
    Long countByCreatedAtBetweenAndBank(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("bank") Bank bank);

    @Query("SELECT c.cardtype, COUNT(c) FROM CardHolder c WHERE c.bank = :bank GROUP BY c.cardtype")
    List<Object[]> countByCardTypeAndBank(@Param("bank") Bank bank);

    @Query("SELECT c.branchcode, COUNT(c) FROM CardHolder c WHERE c.bank = :bank GROUP BY c.branchcode")
    List<Object[]> countByBranchcodeAndBank(@Param("bank") Bank bank);


}