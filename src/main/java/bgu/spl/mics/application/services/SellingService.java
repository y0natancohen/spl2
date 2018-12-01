package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.TransferQueue;

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

    private MoneyRegister register = MoneyRegister.getInstance();

    public SellingService() {
        super("SellingService");
    }

    @Override
    protected void initialize() {
        subscribeEvent(BookOrderEvent.class, this::proccessOrder);

    }


    public void proccessOrder(BookOrderEvent bookOrderEvent){
        // todo sync parts of this
        OrderReceipt receipt;

        Integer price = getBookPrice(bookOrderEvent);
        Customer customer = getCustomer(bookOrderEvent.getCustomerId());
        if (price != -1){
            OrderResult result = tryTake(bookOrderEvent);
            if (result == OrderResult.SUCCESSFULLY_TAKEN){
                register.chargeCreditCard(customer, price);
                receipt = new OrderReceipt(this.getName(),
                                           bookOrderEvent.getCustomerId(),
                                           bookOrderEvent.getBookName(),
                                           bookOrderEvent.getOrderTick());
                deliver(customer);
                complete(bookOrderEvent, receipt);
            }
        }
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
        CheckAvailabilityEvent checkAvbEvent =  new CheckAvailabilityEvent(bookOrderEvent.getBookName());
        return sendEvent(checkAvbEvent).get();
    }

    private Customer getCustomer(int customerId) {
        //todo where are customers being saved?
        return null;
    }

}
