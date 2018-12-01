package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeFromInventoryEvent extends BaseEvent<OrderResult>{
    private String bookName;

    public TakeFromInventoryEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
