package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Event;

public class BookOrderEvent<T> implements Event<T> {
    private String bookName;


    public BookOrderEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
