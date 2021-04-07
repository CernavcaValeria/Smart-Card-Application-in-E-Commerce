package Transfer;

import Transfer.PI;

import java.io.Serializable;


public class PM implements Serializable {
    public PI pi;
    public byte[] signedPaymentDetails;

    public PM() {
    }

    public PM(PI pi, byte[] signedPaymentDetails) {
        this.pi = pi;
        this.signedPaymentDetails = signedPaymentDetails;
    }

    @Override
    public String toString() {
        return "Transfer.PM{" +
                "pi=" + pi +
                '}';
    }
}
