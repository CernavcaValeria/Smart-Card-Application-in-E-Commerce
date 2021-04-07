package Transfer;

import java.io.Serializable;
import java.security.PublicKey;

public class BR implements Serializable {
    public boolean responseBool;
    public String message;
    public String paymentId;
    public Integer amount;
    public Integer balance;
    public Integer NC;

    public BR(boolean responseBool, String message, String paymentId, Integer amount, Integer balance, Integer NC) {
        this.responseBool = responseBool;
        this.message = message;
        this.paymentId = paymentId;
        this.amount = amount;
        this.balance = balance;
        this.NC = NC;
    }

    @Override
    public String toString() {
        return "Transfer.BR{" +
                "responseBool=" + responseBool +
                ", message='" + message + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", NC=" + NC +
                '}';
    }
}