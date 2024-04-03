package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.Entities.DataInputCard.CardHolder;
import tn.monetique.cardmanagment.repository.DataInputCard.CardHolderRepository;
import tn.monetique.cardmanagment.service.Imp.CardHolderServices.CardHolderService;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;

import java.util.List;

@RestController
@RequestMapping("/api/auth/utils")
public class utilitysController {

        @Autowired
        private CardHolderService cardHolderService;
        @Autowired
        IEncryptDecryptservi iEncryptDecryptservi;
        @Autowired
        private CardHolderRepository cardHolderRepository;

        @GetMapping
        public ResponseEntity<List<CardHolder>> getAllCardHolders() {
            List<CardHolder> cardHolders = cardHolderService.getAllCards();

            return new ResponseEntity<>(cardHolders, HttpStatus.OK);
        }
        @PostMapping
        public ResponseEntity<CardHolder> createCardHolder(@RequestBody CardHolder cardHolder) {
            cardHolder.setFirstAccount(iEncryptDecryptservi.encrypt(cardHolder.getFirstAccount()));
            cardHolder.setCardholderNumber(iEncryptDecryptservi.encrypt(cardHolder.getCardholderNumber()));
            CardHolder createdCardHolder = cardHolderRepository.save(cardHolder);
            return new ResponseEntity<>(createdCardHolder, HttpStatus.CREATED);
        }
}

