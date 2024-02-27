package tn.monetique.cardmanagment.SmtpConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmtpConfigService implements IsmtpConfigService {
    @Autowired
    private SmtpConfigRepository smtpConfigRepository;
@Override
    public SmtpConfig getSmtpConfig() {
        return smtpConfigRepository.findById(1L).orElse(null); // Assuming ID 1 always represents the SMTP configuration
    }
@Override
    public SmtpConfig updateSmtpConfig(SmtpConfig newConfig) {
        SmtpConfig existingConfig = smtpConfigRepository.findById(1L).orElse(null);
        if (existingConfig != null) {
            // Update existing config
            existingConfig.setHost(newConfig.getHost());
            existingConfig.setPort(newConfig.getPort());
            existingConfig.setUsername(newConfig.getUsername());
            existingConfig.setPassword(newConfig.getPassword());
            existingConfig.setAuth(newConfig.isAuth());
            existingConfig.setStarttls(newConfig.isStarttls());
            return smtpConfigRepository.save(existingConfig);
        } else {
            // Create new config if it doesn't exist
            newConfig.setId(1L);
            return smtpConfigRepository.save(newConfig);
        }
    }
}

