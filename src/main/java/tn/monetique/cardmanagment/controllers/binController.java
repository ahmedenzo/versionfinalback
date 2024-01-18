package tn.monetique.cardmanagment.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bin;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IbinService;

import java.util.List;

@RestController
    @RequestMapping("/api/auth/bins")
    public class binController {
        @Autowired
      IbinService ibinService;

        @PostMapping("/create")
        public ResponseEntity<Bin> createbin( @RequestBody Bin bin,
                                              @RequestParam String bankname) {
            Bin cretedBin = ibinService.creatBIn(bin, bankname);
            return new ResponseEntity<>(cretedBin, HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Bin> updatebin(@PathVariable Long id, @RequestBody Bin updatedbin) {
            Bin bin = ibinService.Updatebin(updatedbin,id);
            if (bin != null) {
                return new ResponseEntity<>(bin, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> deletebin(@PathVariable Long id) {
            boolean deleted = ibinService.deletebin(id);
            if (deleted) {
                return new ResponseEntity<>("bin deleted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("bin not found or unable to delete.", HttpStatus.NOT_FOUND);
            }
        }

        @GetMapping
        public ResponseEntity<List<Bin>> getAll() {
            List<Bin> banks = ibinService.getallbins();
            return new ResponseEntity<>(banks, HttpStatus.OK);
        }
    @GetMapping("/by-bank/{bankName}")
    public ResponseEntity<List<Bin>> getbinsByBank(@PathVariable String bankName) {
        List<Bin> bins = ibinService.getbinbybank(bankName);
        return new ResponseEntity<>(bins, HttpStatus.OK);
    }



    }




