package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class CreditCard {
    private final int number;
    private AtomicInteger amount;

    public CreditCard(int number, int amount) {
        this.number = number;
        this.amount = new AtomicInteger(amount);
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount.get();
    }

    public void charge(int howMuch) {
        this.amount.addAndGet( - howMuch);
    }
}
