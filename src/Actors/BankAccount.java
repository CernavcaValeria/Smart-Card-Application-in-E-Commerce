package Actors;
public class BankAccount {
    public String owner;
    public String expireDate;
    public String cardNumber;
    public int cvv;
    public Integer balance;

    public BankAccount(String owner, String cardNumber, String expireDate, int cvv, Integer balance) {
        this.owner = owner;
        this.expireDate = expireDate;
        this.cvv = cvv;
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "owner='" + owner + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cvv=" + cvv +
                ", balance=" + balance +
                '}';
    }
}
