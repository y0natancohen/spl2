package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.Objects;

public class CheckAvailabilityEvent implements Event<Integer> {
    private String bookName;

    public CheckAvailabilityEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckAvailabilityEvent that = (CheckAvailabilityEvent) o;
        return Objects.equals(bookName, that.bookName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookName);
    }
}
