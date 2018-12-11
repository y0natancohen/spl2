package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Objects;

public class BookOrderEvent extends FuturedEvent<OrderReceipt> {
    private String bookName;
    private Customer customer;
    private int orderTick;

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick() {
        return orderTick;
    }

    public BookOrderEvent(String bookName, Customer customer, int orderTick) {

        this.bookName = bookName;
        this.customer = customer;
        this.orderTick = orderTick;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookOrderEvent that = (BookOrderEvent) o;
        return orderTick == that.orderTick &&
                Objects.equals(bookName, that.bookName) &&
                Objects.equals(customer, that.customer);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), bookName, customer, orderTick);
    }

    @Override
    public String toString() {
        return "BookOrderEvent{" +
                "bookName='" + bookName + '\'' +
                ", customer=" + customer +
                ", orderTick=" + orderTick +
                '}';
    }
}
