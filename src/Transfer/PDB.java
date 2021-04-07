package Transfer;

import java.io.Serializable;
import java.security.PublicKey;

public class PDB implements Serializable {

    public String paymentId;
    public PublicKey publicKey;
    public Integer amount;
    public String M ;

    public PDB(String paymentId, PublicKey publicKey, Integer amount,String M ) {
        this.paymentId = paymentId;
        this.publicKey = publicKey;
        this.amount = amount;
        this.M = M;
    }

    @Override
    public String toString() {
        return "Transfer.PDB{" +
                "paymentId='" + paymentId + '\'' +
                ", publicKey=" + publicKey +
                ", amount=" + amount +
                '}';
    }
}
