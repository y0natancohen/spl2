package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
    private String bookTitle;
    private int tick;

    public OrderSchedule(String bookTitle, int tick) {
        this.bookTitle = bookTitle;
        this.tick = tick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
