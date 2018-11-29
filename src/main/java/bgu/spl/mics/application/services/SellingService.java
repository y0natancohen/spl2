package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.passiveObjects.*;

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

    MessageBusImpl messageBus = MessageBusImpl.getInstance();

    public SellingService() {
        super("SellingService");
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        subscribeEvent(BookOrderEvent.class, this::proccessOrder);

    }

    private void proccessOrder(BookOrderEvent event){

        CheckAvailabilityEvent checkEvent = new CheckAvailabilityEvent<Integer>(event.getBookName());
        Future<Integer> future = messageBus.sendEvent(checkEvent);
        //todo:bom - hapes badaf
        Integer price = future.get();
        if (price == -1){

        }
    }

}
