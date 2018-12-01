package bgu.spl.mics.application.messages;


import bgu.spl.mics.application.passiveObjects.CreditCard;

public class ReChargeCreditEvent extends BaseEvent<Boolean> {
    private int howMuch;
    private CreditCard card;

    public int getHowMuch() {
        return howMuch;
    }

    public CreditCard getCard() {
        return card;
    }

    public ReChargeCreditEvent(int howMuch, CreditCard card) {
        this.howMuch = howMuch;
        this.card = card;

    }
}
