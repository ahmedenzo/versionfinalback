package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.service.Imp.CAFandPBFfiles.StructureElements.FileGenerationService;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IFileGenerationService;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IHeaderFilesServices;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.ITrailerFileService;

import java.util.List;

@RestController
@RequestMapping("/api/auth/file")
public class FileGenerationController {
    @Autowired
    private FileGenerationService fileGenerationService;
    @Autowired
    IHeaderFilesServices iHeaderFilesServices;
    @Autowired
    ITrailerFileService iTrailerFileService;
    @Autowired
    IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    IFileGenerationService iFileGenerationService;


   @GetMapping("/generate-caf-file")
   public ResponseEntity<FileSystemResource> generateCafFile(@RequestParam List<Long> customerIds, Authentication authentication) {
       return iFileGenerationService.generateCafFile(customerIds,authentication);
   }

    @GetMapping("/generate-PBF-file")
    public ResponseEntity<FileSystemResource> generatePBFFile(@RequestParam List<Long> customerIds,Authentication authentication) {
        return iFileGenerationService.generatePBFFile(customerIds,authentication);
    }
    @GetMapping("/allPBF")
    public ResponseEntity<List<PBFApplicationDataRecord>> getallPBFS(Authentication authentication) {

        List<PBFApplicationDataRecord> pbfs = iApplicationRecordServices.getallpbfbyuser(authentication);
        if (pbfs!=null) {
            return new ResponseEntity<>(pbfs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/allCAF")
    public ResponseEntity<List<CAFApplicationDataRecord>> getallCAFS(Authentication authentication) {

        List<CAFApplicationDataRecord> cafs = iApplicationRecordServices.getallcafbyuser(authentication);
        if (cafs!=null) {
            return new ResponseEntity<>(cafs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
 /* @PostMapping("/generate-header-record")
    public String generateHeaderRecord(@RequestBody FileHeaderRecord headerRecord) {
        return iHeaderFilesServices.createAndGenerateheaderRecord(headerRecord);
    }
    @PostMapping("/generate-org-record")
    public String generateorgHeaderRecord(@RequestBody OrganisationHeaderRecord organisationHeaderRecord) {
        return iHeaderFilesServices.createAndGenerateorganisationRecord(organisationHeaderRecord);
    }
     @PostMapping("/generate-filetrailer-record")
    public String generateTrailerRecord(@RequestBody FileTrailerRecord fileTrailerRecord) {
        return iTrailerFileService.createAndGenerateTrailerRecord(fileTrailerRecord);
    }
    @PostMapping("/generate-orgtrailer-record")
    public String generateorgTrailerRecord(@RequestBody OrganisationTrailerRecord organisationTrailerRecord) {
        return iTrailerFileService.createAndGenerateorgTrailerRecord(organisationTrailerRecord);
    }
    @PostMapping("/generate")
    public ResponseEntity<String> generateCafApplication(@RequestBody CafApplicationRequest request) {
        List<Long> customerIds = request.getCustomerIds();

        String result = iApplicationRecordServices.generateCAFApplicationDataRecordsForCard(customerIds);
        return ResponseEntity.ok(result);
    }*/

