package bgu.spl.mics.application.messages;

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
