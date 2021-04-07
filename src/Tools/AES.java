package Tools;
import Transfer.*;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

public class AES {
    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";


    public static SealedObject encrypt(PublicKey value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            return new SealedObject( value, cipher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static SealedObject encryptBig(BigTransferObject value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            return new SealedObject( value, cipher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static SealedObject encryptPM(PM value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            return new SealedObject( value, cipher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static SealedObject encryptPacketData(PacketData value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            return new SealedObject( value, cipher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PublicKey decrypt(SealedObject encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            return (PublicKey)encrypted.getObject(secretKeySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PublicKey decrypt(SealedObject encrypted, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            return (PublicKey)encrypted.getObject(secretKeySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static BigTransferObject decryptBig(SealedObject encrypted, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            return (BigTransferObject)encrypted.getObject(secretKeySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PacketData decryptPacketData(SealedObject encrypted, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            return (PacketData)encrypted.getObject(secretKeySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PM decryptPM(SealedObject encrypted, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            return (PM)encrypted.getObject(secretKeySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted, String key){
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes( StandardCharsets.UTF_8 ));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8 ), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted)  );
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String getKey(){
        return key;
    }

}