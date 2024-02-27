package tn.monetique.cardmanagment.SmtpConfig;

public interface IsmtpConfigService {
    SmtpConfig getSmtpConfig();

    SmtpConfig updateSmtpConfig(SmtpConfig newConfig);
}
