package tn.monetique.cardmanagment.repository.DataInputCard;

import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {


    Optional<CardHolder> findById( Long CustomerId);
    List<CardHolder>findByBranchcode(String brancheCode);

    boolean existsByCardholderNumber(String CardholderNumber);

    CardHolder findByPassportId(String passport);

    List<CardHolder>findByBank_BankName(String bankname);





}