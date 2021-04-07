package Transfer;
import Actors.Card;
import java.io.Serializable;
import java.security.PublicKey;

public class PI implements Serializable {
    public Card card;
    public String paymentId;
    public PublicKey publicKey;
    public Integer amount;
    public Integer NC;
    public String M;


    public PI(Card card, String paymentId, PublicKey publicKey, Integer amount, Integer NC,String M){
        this.card = card;
        this.paymentId = paymentId;
        this.amount = amount;
        this.publicKey = publicKey;
        this.NC = NC;
        this.M = M;
    }

    @Override
    public String toString() {
        return "Transfer.PI{" +
                "card=" + card +
                ", paymentId='" + paymentId + '\'' +
                ", publicKey=" + publicKey +
                ", amount=" + amount +
                ", NC=" + NC +
                '}';
    }
}
