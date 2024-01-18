package tn.monetique.cardmanagment.repository.DataInputCard;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;

import java.util.List;

public interface GeneratedFileInformationRepository extends JpaRepository<GeneratedFileInformation, Long> {
    GeneratedFileInformation findByFileName(String fileName);
    List<GeneratedFileInformation> findByBankName(String Bankname);
}
