package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.monetique.cardmanagment.SmtpConfig.IsmtpConfigService;
import tn.monetique.cardmanagment.SmtpConfig.SmtpConfig;


@RestController
@RequestMapping("/api/auth/smtp")
public class SmtpConfigController {
    @Autowired
    private IsmtpConfigService smtpConfigService;

    @GetMapping("/config")
    public ResponseEntity<SmtpConfig> getSmtpConfig() {
        SmtpConfig config = smtpConfigService.getSmtpConfig();
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<SmtpConfig> updateSmtpConfig(@RequestBody SmtpConfig newConfig) {
        SmtpConfig updatedConfig = smtpConfigService.updateSmtpConfig(newConfig);
        if (updatedConfig != null) {
            return new ResponseEntity<>(updatedConfig, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<SmtpConfig> createSMtp(@RequestBody SmtpConfig newConfig) {
        SmtpConfig updatedConfig = smtpConfigService.updateSmtpConfig(newConfig);
        if (updatedConfig != null) {
            return new ResponseEntity<>(updatedConfig, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}


