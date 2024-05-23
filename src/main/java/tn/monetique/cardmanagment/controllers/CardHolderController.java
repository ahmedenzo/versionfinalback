package tn.monetique.cardmanagment.controllers;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.CAFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFApplicationDataRecord;
import tn.monetique.cardmanagment.Entities.ApplicationDataRecord.PBFBalanceHistory;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.Entities.DataInputCard.GeneratedFileInformation;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AgentBankRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.RefreshTokenRepository;
import tn.monetique.cardmanagment.repository.userManagmentRepo.AdminBankRepository;
import tn.monetique.cardmanagment.repportandstatistique.Cardstatistique;
import tn.monetique.cardmanagment.repportandstatistique.IcardStat;
import tn.monetique.cardmanagment.security.services.RefreshTokenService;
import tn.monetique.cardmanagment.security.services.UserDetailsImpl;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;
import tn.monetique.cardmanagment.service.Interface.PBFCAF.IApplicationRecordServices;
import tn.monetique.cardmanagment.service.Interface.Card.IGeneratePortFile;
import tn.monetique.cardmanagment.service.Interface.Card.IcardHolderService;
import tn.monetique.cardmanagment.service.Interface.GestionUserInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/auth/card")
public class    CardHolderController {
    @Autowired
    IcardHolderService icardHolderService;
    @Autowired
    IApplicationRecordServices iApplicationRecordServices;
    @Autowired
    IEncryptDecryptservi iEncryptDecryptservi;
    @Autowired
    UserService userService;
    @Autowired
    private IGeneratePortFile iGeneratePortFile;
    @Autowired
    RefreshTokenService refreshTokenService;
   @Autowired
    IcardStat icardStat;
    @Autowired
    private AdminBankRepository adminBankRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    AgentBankRepository agentBankRepository;

    @Autowired
    private CardHolderRepository cardHolderRepository;



    @PostMapping("/CreateNewCard")
    public ResponseEntity<?> CreateNewCard(@RequestParam Long selectedBinId , @RequestBody CardHolder cardHolder,Authentication authentication ) {
        try {
            CardHolder addedCarddatainPut = icardHolderService.createNewCard(cardHolder, selectedBinId, authentication);
            return new ResponseEntity<>(addedCarddatainPut, HttpStatus.CREATED);
        } catch (RuntimeException e) { // Catch the specific exception thrown by createNewCard method
            return new ResponseEntity<>("User already has a card", HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // Catch other unexpected exceptions
            return new ResponseEntity<>("Failed to create card", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{customerId}/update/{selectedBinId}")
    public ResponseEntity<Object> updateCardHolderData(
            @PathVariable Long customerId,
            @PathVariable Long selectedBinId,
            @RequestBody CardHolder updatedData,
            Authentication authentication) {

        {
            CardHolder updatedCardHolder = icardHolderService.UpdateDataInput(customerId, selectedBinId, updatedData, authentication);

            if (updatedCardHolder != null) {
                return new ResponseEntity<>(updatedCardHolder, HttpStatus.OK);
            } else {
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }
        }
    }
    @PutMapping("/{customerId}/Newoperation/{selectedBinId}")
    public ResponseEntity<Object> updateCardNewoperation(
            @PathVariable Long customerId,
            @PathVariable Long selectedBinId,
            @RequestBody CardHolder updatedData,
            Authentication authentication) {

        {
            CardHolder updatedCardHolder = icardHolderService.updategeneratedcard(customerId, selectedBinId, updatedData, authentication);

            if (updatedCardHolder != null) {
                return new ResponseEntity<>(updatedCardHolder, HttpStatus.OK);
            } else {
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }
        }
    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Object> deleteDatainput(@PathVariable Long customerId) {
        try {
            icardHolderService.deleteDataInput(customerId);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Data Card input file deleted\"}");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"Card not found\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Cannot delete confirmed card holder\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"success\": false, \"message\": \"An error occurred while processing the request\"}");
        }

    }
    @GetMapping("/{customerId}")
    public ResponseEntity<CardHolder> getCardHolderById(@PathVariable Long customerId) {
        CardHolder cardHolder = icardHolderService.getCardHolderById(customerId);

        if (cardHolder != null) {
            return new ResponseEntity<>(cardHolder, HttpStatus.OK);
        } else {
            // Return a not found response with HTTP status 404 or customize it as needed.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<CardHolder>> getAllCardHolders(Authentication authentication) {

        List<CardHolder> cardHolders = icardHolderService.getAllCardHolders(authentication);
        if (cardHolders!=null) {
            return new ResponseEntity<>(cardHolders, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{customerId}/confirmation")
    public ResponseEntity<Object> Comfirmdatainputcart(@PathVariable Long customerId) {

        CardHolder cardHolder = icardHolderService.Confirmation(customerId);

        if (cardHolder != null) {
            if(cardHolder.isConfirmation()){
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }else
                return new ResponseEntity<>(cardHolder, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/generateStructureDataInput")
    public ResponseEntity<String> generateStructureDataInputForCards(@RequestParam List<Long> customerIds) {
        String structureDataInput = iGeneratePortFile.generateStructuredatainputforCards(customerIds);

        if (!structureDataInput.isEmpty()) {
            return new ResponseEntity<>(structureDataInput, HttpStatus.OK);
        } else {
            // Return a not found response with HTTP status 404 or customize it as needed.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/generateDataInput")
    public ResponseEntity<?> generateDataInputForCards(
            @RequestParam List<Long> customerIds, Authentication authentication) {

        try {
            // Your service implementation
            ResponseEntity<FileSystemResource> response = iGeneratePortFile.generateDatainputforcards(customerIds, authentication);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            // Handle exceptions here, and return an appropriate response
            String errorMessage = "An error occurred while generating data input for cards: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/files/all")
    public ResponseEntity<List<GeneratedFileInformation>> getAllfilesbyuser(Authentication authentication) {

        List<GeneratedFileInformation> files =iGeneratePortFile.getallfilesbyuser(authentication);
        if (files!=null) {
            return new ResponseEntity<>(files, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("download/{filename}")
    public ResponseEntity<FileSystemResource> downloadGeneratedFile(@PathVariable String filename) {
        return iGeneratePortFile.downloadGeneratedFile(filename);
    }
    @PutMapping("/pbf-record/{pbfId}")
    public ResponseEntity<PBFApplicationDataRecord> updatePBFrecord(@PathVariable Long pbfId, @RequestBody PBFApplicationDataRecord newpbfApplicationDataRecord) {

        try {
            PBFApplicationDataRecord updatedPBF = iApplicationRecordServices.updatePBFrecord(pbfId, newpbfApplicationDataRecord);
            return ResponseEntity.ok(updatedPBF);
        } catch (IllegalArgumentException e) {
            // Handle the exception appropriately
            return ResponseEntity.badRequest().body(null); // Return a bad request response
        } catch (Exception ex) {
            // Handle other exceptions if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return an internal server error response
        }

    }
    @GetMapping("/pbf-record/history/{pbfRecordId}")
    public ResponseEntity<?> getPBFRecordHistory(@PathVariable Long pbfRecordId) {
        try {
            List<PBFBalanceHistory> history = iApplicationRecordServices.getHistoryForPBFRecord(pbfRecordId);
            return ResponseEntity.ok(history);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PBF Record with ID " + pbfRecordId + " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching PBF Record history");
        }


        }

            @PutMapping("/caf-record/{CafId}")
    public ResponseEntity<CAFApplicationDataRecord> updateCAFrecord(@PathVariable Long CafId, @RequestBody CAFApplicationDataRecord newCafApplicationDataRecord) {
        CAFApplicationDataRecord updatedCAF = iApplicationRecordServices.updateCAFrecord(CafId, newCafApplicationDataRecord);
        if (updatedCAF != null) {
            return ResponseEntity.ok(updatedCAF);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllCardHolderByBank/{bankname}")
    public ResponseEntity<?> getAllCardHolderByBank(@PathVariable String bankname) {
        List<CardHolder> cardHolders = cardHolderRepository.findByBank_BankName(bankname);

        for (CardHolder cardHolder : cardHolders) {
            String numcard = iEncryptDecryptservi.encrypt(cardHolder.getCardholderNumber());
            cardHolder.setCardholderNumber(numcard);
            cardHolder.setFirstAccount(numcard);
            cardHolderRepository.save(cardHolder);
        }

        return new ResponseEntity<>(cardHolders, HttpStatus.CREATED);
    }

/////////////////////////////////statistique////////////////////////////


        @GetMapping("/cardsbyinterval")
        public ResponseEntity<?> getCardHoldersByDateIntervalAndBank(
                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                Authentication authentication) {

            try {
                List<CardHolder> cardHolders = icardStat.getCardHoldersByDateIntervalAndBank(startDate, endDate, authentication);
                return ResponseEntity.ok(cardHolders);
            } catch (AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: " + ex.getMessage());
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
            }
        }
    @GetMapping("/downloadPdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam("startDate") LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate,
                                              Authentication authentication) {
        try {
            List<CardHolder> cardHolders = icardStat.getCardHoldersByDateIntervalAndBank(startDate, endDate, authentication);
            byte[] pdfBytes = icardStat.generatePdf(cardHolders,startDate,endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Cards.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cardsbalancesbyinterval")
    public ResponseEntity<?> getblancesByDateIntervalAndBank(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        try {
            List<PBFApplicationDataRecord> records = icardStat.getPBFApplicationDataRecordsByDateIntervalAndBank(startDate, endDate, authentication);
            return ResponseEntity.ok(records);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    @GetMapping("/downloadBalancePdf")
    public ResponseEntity<byte[]> downloadbalancePdf(@RequestParam("startDate") LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate,
                                              Authentication authentication) {
        try {
            List<PBFApplicationDataRecord> records = icardStat.getPBFApplicationDataRecordsByDateIntervalAndBank(startDate, endDate, authentication);
            byte[] pdfBytes = icardStat.generatePdfpbf(records,startDate,endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "Balance.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






////////////////////////////////Not Used/////////////////////////////








 /*
  @GetMapping("test/CardHolder/pbf/{CustomerId}")
    public ResponseEntity<UpdateResponsePayload> getCardHolderandPbfById(@PathVariable Long CustomerId) {
            CardHolder cardHolder = icardHolderService.getCardHolderById(CustomerId);
            PBFApplicationDataRecord pbfApplicationDataRecord = iApplicationRecordServices.getPBFApplicationDataRecordByCustomerId(CustomerId);

     if (cardHolder != null && pbfApplicationDataRecord != null) {
        UpdateResponsePayload responsePayload = new UpdateResponsePayload(cardHolder, pbfApplicationDataRecord);
        System.out.println("success");
        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    } else {
        System.out.println("failed");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}



 @DeleteMapping("/delete-records/{customerId}")
    public ResponseEntity<String> deleteRecords(@PathVariable Long customerId) {
        try {
            icardHolderService.deleteRecords(customerId);
            return ResponseEntity.ok("Records deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }


            @PutMapping("/{customerId}/genreted")
    public ResponseEntity<Object> generatedcard(@PathVariable Long customerId, @RequestHeader String RefrechTokenV) {
        String usernam = userService.getCurrentUser().getUsername();
        Long userid = userService.getCurrentUser().getId();
        Optional<BankAdmin> optionalUser = adminBankRepository.findByUsername(usernam);
        if (optionalUser.isPresent()) {
            if (userService.isUserIdMatchingToken(userid, RefrechTokenV)) {
                CardHolder cardHolder = icardHolderService.checkgeneratedfile(customerId);

                if (cardHolder != null) {
                    return new ResponseEntity<>(cardHolder, HttpStatus.OK);

                } else {
                    return new ResponseEntity<>("Cardholder not found", HttpStatus.NOT_FOUND);
                }
            } else {
                // Token is invalid or expired, return an error response
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("open an other session refrechtoken invalid ");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid Token");
        }
    }


    @GetMapping("test/pbf/{CustomerId}")
    public ResponseEntity<PBFApplicationDataRecord> getPbfById(@PathVariable Long CustomerId) {

        PBFApplicationDataRecord pbfApplicationDataRecord = iApplicationRecordServices.getPBFApplicationDataRecordByCustomerId(CustomerId);

        if (pbfApplicationDataRecord != null) {

            System.out.println("success");
            return new ResponseEntity<>(pbfApplicationDataRecord, HttpStatus.OK);
        } else {
            System.out.println("failed");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    }*/


   /* @GetMapping("/CardHolderbyagence")
    public ResponseEntity<Object> getcardperagency(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username =userDetails.getUsername();
        AgentBank agentBank = agentBankRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Fake user"));
        String branchcode = agentBank.getAgence().getBranchCode();
        System.out.println("agency"+agentBank.getAgence());
        List<CardHolder> cardHolders = icardHolderService.getAllCardHolderbyagency(branchcode);

        if (!cardHolders.isEmpty()) {
            return new ResponseEntity<>(cardHolders, HttpStatus.OK);
        } else {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }
    }*/



    /*
    @PutMapping("/{customerId}/resetCardGenerated")
public ResponseEntity<Object> resetCardGenerated(@PathVariable Long customerId) {
    try {
        CardHolder cardHolder = icardHolderService.resetCardGenerated(customerId);
        return new ResponseEntity<>(cardHolder, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        return new ResponseEntity<>("An error occurred while resetting the card generated status.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @GetMapping("/getCardsByCustomerIds")
    public ResponseEntity<List<CardHolder>> getCardsByCustomerIds(@RequestParam List<Long> customerIds) {
        List<CardHolder> cards = icardHolderService.getCardsbyCustomerIds(customerIds);

        if (!cards.isEmpty()) {
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } else {
            // Return a not found response with HTTP status 404 or customize it as needed.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    */

}









