package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
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

    public SellingService() {
        super("SellingService");
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        subscribeEvent(BookOrderEvent.class, this::proccessOrder);

    }

    public void proccessOrder(BookOrderEvent bookOrderEvent){
        CheckAvailabilityEvent<Integer> checkEvent =  new CheckAvailabilityEvent<>(bookOrderEvent.getBookName());
        Integer price = sendEvent(checkEvent).get();


        boolean success = true;
        complete(bookOrderEvent, success);
    }

}
