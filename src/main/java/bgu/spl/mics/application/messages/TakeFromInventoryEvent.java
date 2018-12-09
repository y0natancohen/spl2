package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.Objects;

public class TakeFromInventoryEvent extends BaseEvent<OrderResult>{
    private String bookName;

    public TakeFromInventoryEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TakeFromInventoryEvent that = (TakeFromInventoryEvent) o;
        return Objects.equals(bookName, that.bookName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), bookName);
    }
}
