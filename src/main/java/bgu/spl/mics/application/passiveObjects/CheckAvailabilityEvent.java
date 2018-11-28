package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent<T> implements Event<T> {
    private String bookName;

    public CheckAvailabilityEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
