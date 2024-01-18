package tn.monetique.cardmanagment.service.Interface.Card;

public interface IEncryptDecryptservi {
    String encrypt(String data);
    String decrypt(String encryptedData);
}