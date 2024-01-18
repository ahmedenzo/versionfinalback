package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.Entities.ConfigBank.Agence;

import tn.monetique.cardmanagment.service.Interface.BankConfig.IagenceService;

import java.util.List;

@RestController
@RequestMapping("/api/auth/agencies")
public class AgenceController {



    @Autowired
    private IagenceService iagenceService;

    @PostMapping("/create")
    public ResponseEntity<Agence> createAgency(
            @RequestBody Agence agence,
            @RequestParam String bankname) {
        Agence createdAgency = iagenceService.createAgence(agence, bankname);
        return new ResponseEntity<>(createdAgency, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Agence> updateAgency(@PathVariable Long id, @RequestBody Agence updatedAgency) {
        Agence agency = iagenceService.UpdateAgence(id, updatedAgency);
        if (agency != null) {
            return new ResponseEntity<>(agency, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAgency(@PathVariable Long id) {
        boolean deleted = iagenceService.deleteAgence(id);
        if (deleted) {
            return new ResponseEntity<>("Agency deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Agency not found or unable to delete.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Agence>> getAllAgencies() {
        List<Agence> agencies = iagenceService.getallagence();
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }

    @GetMapping("/by-bank/{bankName}")
    public ResponseEntity<List<Agence>> getAgenciesByBank(@PathVariable String bankName) {
        List<Agence> agencies = iagenceService.getallagencebyBank(bankName);
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }
}

