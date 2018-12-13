package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class SellingService extends MicroService {
    private static final AtomicInteger timeTrack = new AtomicInteger();
    private final MoneyRegister moneyRegister = MoneyRegister.getInstance();
    private CountDownLatch countDownLatch;

    public SellingService(CountDownLatch countDownLatch, int seq) {
        super("SellingService " + seq);
        this.countDownLatch = countDownLatch;

    }

    @Override
    protected void initialize() {
        subscribeEvent(BookOrderEvent.class, this::processOrder);
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> timeTrack.set(tickBroadcast.getCurrentTick()));
        subscribeBroadcast(PoisonPill.class, poison -> terminate());
        countDownLatch.countDown();
    }


    public void processOrder(BookOrderEvent bookOrderEvent) {
        if (BookStoreRunner.debug){System.out.println("inside SellingService.processOrder()");}
        int processTick = timeTrack.get();
        OrderReceipt receipt = null;
        Integer price = getBookPrice(bookOrderEvent);
        boolean success = false;
        if (price != -1 && bookOrderEvent.getCustomer().getCreditCard().getAmount() >= price) {
            synchronized (bookOrderEvent.getCustomer()) { // 2 orders from same customer will be synced
                if (bookOrderEvent.getCustomer().getCreditCard().getAmount() >= price) {
                    OrderResult result = tryTake(bookOrderEvent);
                    if (result == OrderResult.SUCCESSFULLY_TAKEN) {
                        moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), price);
                        success = true;
                    }
                }
            }
        }
        if (success) {
            receipt = new OrderReceipt(bookOrderEvent.getOrderId(), this.getName(),
                    bookOrderEvent.getCustomer().getId(),
                    bookOrderEvent.getBookName(),
                    price,
                    timeTrack.get(),
                    bookOrderEvent.getOrderTick(),
                    processTick);
            bookOrderEvent.getCustomer().addReciept(receipt);
            moneyRegister.file(receipt);
            deliver(bookOrderEvent.getCustomer());
        }
        complete(bookOrderEvent, receipt);
    }

    private void deliver(Customer customer) {
        DeliveryEvent deliveryEvent = new DeliveryEvent(customer.getAddress(), customer.getDistance());
        sendEvent(deliveryEvent);
    }

    private OrderResult tryTake(BookOrderEvent bookOrderEvent) {
        TakeFromInventoryEvent takeInvEvent = new TakeFromInventoryEvent(bookOrderEvent.getBookName());
        return sendEvent(takeInvEvent).get();
    }

    private Integer getBookPrice(BookOrderEvent bookOrderEvent) {
        CheckAvailabilityEvent checkAvbEvent = new CheckAvailabilityEvent(bookOrderEvent.getBookName());
        return sendEvent(checkAvbEvent).get();
    }

}
