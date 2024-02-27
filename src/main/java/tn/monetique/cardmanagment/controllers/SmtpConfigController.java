package tn.monetique.cardmanagment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/update")
    public ResponseEntity<String> updateSmtpConfig(@RequestBody SmtpConfig newConfig) {
        SmtpConfig updatedConfig = smtpConfigService.updateSmtpConfig(newConfig);
        if (updatedConfig != null) {
            return ResponseEntity.ok("SMTP configuration updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to update SMTP configuration.");
        }
    }
}


