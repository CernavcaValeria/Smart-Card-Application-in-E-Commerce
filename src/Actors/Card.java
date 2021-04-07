package Actors;

import java.io.Serializable;

public class Card implements Serializable {
    public String customerName;
    public String cardNumber;
    public String expireDate;
    public int cvv;

    public Card(String customerName, String cardNumber, String expire, int cvv){
        this.cardNumber = cardNumber;
        this.customerName = customerName;
        this.expireDate = expire;
        this.cvv = cvv;
    }
    public Card(String customerName, String cardNumber, String expire){
        this.cardNumber = cardNumber;
        this.customerName = customerName;
        this.expireDate = expire;

    }

    @Override
    public String toString() {
        return "Actors.Card{" +
                "customerName='" + customerName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", cvv=" + cvv +
                '}';
    }
}
