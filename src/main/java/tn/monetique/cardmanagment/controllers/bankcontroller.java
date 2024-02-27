package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;

import tn.monetique.cardmanagment.Entities.ConfigBank.BankFTPConfig;
import tn.monetique.cardmanagment.payload.request.ConfigureDataRequest;
import tn.monetique.cardmanagment.payload.response.ConfigureDataResponse;
import tn.monetique.cardmanagment.service.Interface.BankConfig.*;

import java.util.List;
import java.util.Optional;

@RestController
    @RequestMapping("/api/auth/banks")
    public class bankcontroller {
        @Autowired
        private Ibankservice ibankservice;
        @Autowired
        IAtmDataService iAtmDataService;
        @Autowired
        IEmvDataServices iEmvDataServices;
        @Autowired
        IPOSPBFXDServices ipospbfxdServices;
        @Autowired
        IPosDataService iPosDataService;
        @Autowired
    IftpConfigurationService iftpConfigurationService;

        @PostMapping
        public ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
            Bank createdBank = ibankservice.creatBank(bank);
            return new ResponseEntity<>(createdBank, HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Bank> updateBank(@PathVariable Long id, @RequestBody Bank updatedBank) {
            Bank bank = ibankservice.updateBank(id, updatedBank);
            if (bank != null) {
                return new ResponseEntity<>(bank, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteBank(@PathVariable Long id) {
            boolean deleted = ibankservice.deleteBank(id);
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        @GetMapping
        public ResponseEntity<List<Bank>> getAllBanks() {
            List<Bank> banks = ibankservice.getallbank();
            return new ResponseEntity<>(banks, HttpStatus.OK);
        }

    @PostMapping("/configureData/{BinID}")
    public ResponseEntity<ConfigureDataResponse> configureData(@RequestBody ConfigureDataRequest request,
                                                               @PathVariable Long BinID) {
        try {
            ConfigureDataResponse configureDataResponse = ibankservice.configureData(request,BinID);
            return ResponseEntity.ok(configureDataResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    ////////////FTP config /////////////////////
    @PostMapping("/ftpsave")
    public ResponseEntity<BankFTPConfig> saveOrUpdateFTPConfiguration(@RequestBody BankFTPConfig ftpConfiguration) {
        // You can perform validation here if needed

        // Save or update FTP configuration
        BankFTPConfig savedFTPConfiguration = iftpConfigurationService.saveFTPConfiguration(ftpConfiguration);

        return ResponseEntity.ok(savedFTPConfiguration);
    }

    // Endpoint to retrieve FTP configuration by bank ID
    @GetMapping("/getftp-by-bank/{bankId}")
    public ResponseEntity<BankFTPConfig> getFTPConfigurationByBank(@PathVariable Long bankId) {
        // Retrieve bank by ID
        Optional<Bank> optionalBank = ibankservice.getbankbyid(bankId);
        Bank bank = optionalBank.get();
        if (bank == null) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve FTP configuration by bank
        BankFTPConfig ftpConfiguration = iftpConfigurationService.getFTPConfigurationByBank(bank);
        if (ftpConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ftpConfiguration);
    }

    // Endpoint to delete FTP configuration by ID
    @DeleteMapping("/ftpelete/{ftpConfigId}")
    public ResponseEntity<Void> deleteFTPConfiguration(@PathVariable Long ftpConfigId) {
        // Retrieve FTP configuration by ID
        BankFTPConfig ftpConfiguration = iftpConfigurationService.getFTPConfigurationById(ftpConfigId);
        if (ftpConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // Delete FTP configuration
        iftpConfigurationService.deleteFTPConfiguration(ftpConfiguration);

        return ResponseEntity.noContent().build();
    }
}




