package Tools;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.util.Arrays;

public class Pack implements Serializable {
    public String symmetricKey;
    public SealedObject sealedObject;
    public String sid;
    public byte[] sigM;
    public String message;

    public Pack(){}

    public Pack(SealedObject object, String key){
        this.sealedObject = object;
        this.symmetricKey = key;
    }
    public Pack(SealedObject object){
        this.sealedObject = object;
    }
    public Pack( String str){
        this.symmetricKey = str;
        this.message = str;
    }

    public Pack(String id, byte[] signedId){
        this.sid = id;
        this.sigM = signedId;
    }

    @Override
    public String toString() {
        return "Tools.Pack{" +
                "symmetricKey='" + symmetricKey + '\'' +
                ", privateK=" + sealedObject +
                ", sid='" + sid + '\'' +
                ", sigM=" + Arrays.toString( sigM ) +
                '}';
    }
}

