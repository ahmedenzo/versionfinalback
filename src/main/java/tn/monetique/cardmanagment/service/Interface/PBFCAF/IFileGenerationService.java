package tn.monetique.cardmanagment.service.Interface.PBFCAF;


import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IFileGenerationService {

    ResponseEntity<FileSystemResource> generateCafFile(List<Long> customerIds, Authentication authentication);

    ResponseEntity<FileSystemResource> generatePBFFile(List<Long> customerIds, Authentication authentication);

}
