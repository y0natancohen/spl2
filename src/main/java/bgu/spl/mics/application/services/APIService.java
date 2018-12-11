package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

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

    public APIService(Customer customer, CountDownLatch countDownLatch) {
        super("APIService");
        this.customer = customer;
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            customer.getOrderSchedule().stream()
                    .filter(orderSchedule -> orderSchedule.getTick() == tickBroadcast.getCurrentTick())
                    .forEach(relevantOrder -> {
                        BookOrderEvent bookOrderEvent =
                                new BookOrderEvent(relevantOrder.getBookTitle(), customer, relevantOrder.getTick());
                        System.out.println(String.format("service: %s sending order event: %s", getName(), bookOrderEvent));
                        sendEvent(bookOrderEvent);
                    });
        });
        countDownLatch.countDown();
    }
}
