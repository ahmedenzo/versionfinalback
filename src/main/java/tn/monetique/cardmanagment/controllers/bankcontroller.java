package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;

import tn.monetique.cardmanagment.payload.request.ConfigureDataRequest;
import tn.monetique.cardmanagment.payload.response.ConfigureDataResponse;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IAtmDataService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IEmvDataServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPOSPBFXDServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPosDataService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.Ibankservice;

import java.util.List;

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
    }



