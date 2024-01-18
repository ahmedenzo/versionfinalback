package tn.monetique.cardmanagment.repository.Bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {
    List<Bin> findBinByBank_BankName(String bankName);

    List<Bin> findAgencesByBank_BankId(Long bankid);
    Optional<Bin> findById(Long Id);

}
