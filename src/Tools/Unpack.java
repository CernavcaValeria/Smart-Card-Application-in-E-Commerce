package Tools;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.util.Arrays;

public class Unpack implements Serializable{
    public String sid;
    public byte[] sigM;
    public String symmetricKey;
    public SealedObject sealedObject;
    public String message;

    public Unpack(){}

    public Unpack(String sid, byte[] sigM){
        this.sid = sid;
        this.sigM = sigM;
    }
    public Unpack(SealedObject object, String key){
        this.symmetricKey = key;
        this.sealedObject = object;
    }
    public Unpack( String str){
        this.symmetricKey = str;
        this.message = str;
    }
    @Override
    public String toString() {
        return "Tools.Unpack{" +
                "sid='" + sid + '\'' +
                ", sigM=" + Arrays.toString( sigM ) +
                ", symmetricKey='" + symmetricKey + '\'' +
                ", privateK=" + sealedObject +
                '}';
    }
}

