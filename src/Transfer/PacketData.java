package Transfer;

import javax.crypto.SealedObject;
import java.io.Serializable;

public class PacketData implements Serializable {
    public SealedObject PM;
    public byte[] paymentDetails;
    public byte[] signedPaymentDetails;
    public BR bankResponseData;

    public PacketData(byte[] paymentDetails, byte[] signedPaymentDetails) {
        this.paymentDetails = paymentDetails;
        this.signedPaymentDetails = signedPaymentDetails;
    }
    public PacketData(BR bankResponseData, byte[] signedPaymentDetails) {
        this.bankResponseData = bankResponseData;
        this.signedPaymentDetails = signedPaymentDetails;
    }

    public PacketData(SealedObject pm, byte[] signedPaymentDetails) {
        this.PM = pm;
        this.signedPaymentDetails = signedPaymentDetails;
    }
}