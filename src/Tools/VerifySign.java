package Tools;

import java.security.*;

public class VerifySign {

    public VerifySign(){}

    public static boolean verify(PublicKey publicKey, byte[] value , byte[] signedValue) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);
        sign.update(value);
        return sign.verify(signedValue);
    }
}
