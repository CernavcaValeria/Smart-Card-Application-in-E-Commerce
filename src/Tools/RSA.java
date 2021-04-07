package Tools;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSA {
    public PublicKey publicKey;
    private PrivateKey privateKey;


    public RSA() throws  Exception{
        KeyPair keyPair = buildKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }
    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public String encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    public String decrypt(PrivateKey privateKey, String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherBytes = (cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        return new String(cipherBytes);
    }
}







/*
    public String encrypt(PublicKey publicKey, byte[] transferObject) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherBytes = cipher.doFinal(transferObject);
        return Base64.getEncoder().encodeToString(cipherBytes);
    }
*/