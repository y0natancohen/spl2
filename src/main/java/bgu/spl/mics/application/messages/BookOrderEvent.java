package bgu.spl.mics.application.messages;

public class BookOrderEvent<T> extends BaseEvent<T> {
    private String bookName;


    public BookOrderEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
