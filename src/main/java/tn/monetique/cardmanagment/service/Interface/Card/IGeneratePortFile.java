package tn.monetique.cardmanagment.service.Interface.Card;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;

import java.util.List;

public interface IGeneratePortFile {
    String generateStructuredatainputforCards(List<Long> customerIds);

    ResponseEntity<FileSystemResource> generateDatainputforcards(List<Long> customerIds, Authentication authentication);
    GeneratedFileInformation saveGeneratedFileInformation(String fileName, String filePath, String username, String bankname, String filetype);

    List<GeneratedFileInformation> getallfilesbyuser(Authentication authentication);

    ResponseEntity<FileSystemResource> downloadGeneratedFile(String fileName);

    GeneratedFileInformation getfilebyid(Long fileId);
}
