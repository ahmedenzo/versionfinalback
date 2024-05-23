package tn.monetique.cardmanagment.repository.DataInputCard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
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


    @Query("SELECT c FROM CardHolder c WHERE c.bank.bankId = :bankId AND c.createdAt BETWEEN :startDate AND :endDate AND c.cardgenerated = true ORDER BY c.statuscard")
    List<CardHolder> findCardHoldersByDateIntervalAndBank(@Param("startDate") Timestamp  startDate,
                                                          @Param("endDate") Timestamp endDate,
                                                          @Param("bankId") Long bankId);



}