package Transfer;

import java.io.Serializable;

public class PO implements Serializable {

    public String orderDesc;
    public String paymentId;
    public Integer amount;
    public Integer NC;



    public PO( String orderDesc, String paymentId, Integer amount, Integer NC) {
        this.orderDesc = orderDesc;
        this.paymentId = paymentId;
        this.amount = amount;
        this.NC = NC;
    }

    @Override
    public String toString() {
        return "Transfer.PO{" +
                ", orderDesc='" + orderDesc + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", NC=" + NC +
                '}';
    }
}
