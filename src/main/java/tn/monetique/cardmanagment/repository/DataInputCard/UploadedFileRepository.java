package tn.monetique.cardmanagment.repository.DataInputCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.monetique.cardmanagment.Entities.DataInputCard.UploadedFile;


@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
