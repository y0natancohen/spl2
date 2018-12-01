package bgu.spl.mics.application.messages;

public class TakeBookEvent extends BaseEvent {
    private String bookName;

    public TakeBookEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
