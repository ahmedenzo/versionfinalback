package tn.monetique.cardmanagment.security.services;

import org.springframework.stereotype.Service;
import tn.monetique.cardmanagment.service.Interface.Card.IEncryptDecryptservi;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
@Service
public class EncryptionDecryptionService implements IEncryptDecryptservi {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public EncryptionDecryptionService() {

            try {
                File keysFolder = new File("Cartmanagment_keys");

                if (!keysFolder.exists()) {
                    if (keysFolder.mkdirs()) {
                        System.out.println("Keys folder created.");
                    } else {
                        System.out.println("Failed to create keys folder.");
                        return;
                    }
                }

                File publicKeyFile = new File(keysFolder, "keyspublic_key.pub");
                File privateKeyFile = new File(keysFolder, "keysprivate_key.pem");

                if (!publicKeyFile.exists() || !privateKeyFile.exists()) {
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                    keyPairGenerator.initialize(1024);
                    KeyPair keyPair = keyPairGenerator.generateKeyPair();
                    publicKey = keyPair.getPublic();
                    privateKey = keyPair.getPrivate();

                    saveKeyToFile(publicKeyFile.getPath(), publicKey);
                    saveKeyToFile(privateKeyFile.getPath(), privateKey);

                    System.out.println("Key pair generated and saved.");
                } else {
                    publicKey = loadPublicKeyFromFile(publicKeyFile.getPath());
                    privateKey = loadPrivateKeyFromFile(privateKeyFile.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public String encrypt(String data) {
        try {
            if (publicKey == null) {
                throw new IllegalStateException("Public key is null. Make sure the keys are properly initialized.");
            }
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedData) {
        try {
            if (privateKey == null) {
                throw new IllegalStateException("Private key is null. Make sure the keys are properly initialized.");
            }
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey loadPublicKeyFromFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }

            byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PrivateKey loadPrivateKeyFromFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }

            byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveKeyToFile(String fileName, Key key) {
        try {
            byte[] keyBytes = key.getEncoded();
            Files.write(Paths.get(fileName), keyBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
