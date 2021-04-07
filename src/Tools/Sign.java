package Tools;
import java.security.*;

public class Sign {
    public Sign(){}
    public static byte[] sign(PrivateKey privateKey, byte[] value) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(value);
        return sign.sign();
    }

}
