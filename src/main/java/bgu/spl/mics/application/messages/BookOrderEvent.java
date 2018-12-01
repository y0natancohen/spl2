package bgu.spl.mics.application.messages;

public class BookOrderEvent<T> extends BaseEvent<T> {
    private String bookName;
    private int customerId;
    private int orderTick;

    public int getCustomerId() {
        return customerId;
    }

    public int getOrderTick() {
        return orderTick;
    }

    public BookOrderEvent(String bookName, int customerId, int orderTick) {

        this.bookName = bookName;

        this.customerId = customerId;
        this.orderTick = orderTick;
    }

    public String getBookName() {
        return bookName;
    }
}
