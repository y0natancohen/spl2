package bgu.spl.mics.application.passiveObjects;

public class CreditCard {
    private int number;
    private int  amount;

    public CreditCard(int number, int amount) {
        this.number = number;
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public void charge(int howMuch) {
        // TODO: sync here
        // TODO: what if we dont have money?
        this.amount = amount - howMuch;
    }
}
