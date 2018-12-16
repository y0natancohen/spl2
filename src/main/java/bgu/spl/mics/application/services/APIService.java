package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.PoisonPill;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class APIService extends MicroService {
    private Customer customer;
    private CountDownLatch countDownLatch;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public APIService(Customer customer, CountDownLatch countDownLatch) {
        super("APIService " + customer.getId());
        this.customer = customer;
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PoisonPill.class, poison -> terminate());
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            customer.getOrderSchedule().stream()
                    .filter(orderSchedule -> orderSchedule.getTick() == tickBroadcast.getCurrentTick())
                    .forEach(relevantOrder -> {
                        if (BookStoreRunner.debug){System.out.println(String.format("customer ordering now is: %s", customer));}
                        BookOrderEvent bookOrderEvent =
                                new BookOrderEvent(relevantOrder.getBookTitle(), customer, relevantOrder.getTick(),
                                        IndexDispatcher.getInstance().getNextId());
                        sendEvent(bookOrderEvent);
                    });
        });
        countDownLatch.countDown();
    }
}
