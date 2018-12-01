package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

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

    public APIService() {
        super("Change_This_Name");
        // TODO Implement this
    }

    public APIService(Customer customer) {
        this();
        this.customer = customer;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            customer.getOrderSchedule().stream()
                    .filter(orderSchedule -> orderSchedule.getTick() == tickBroadcast.getCurrentTick())
                    .forEach(relevantOrder ->
                            sendEvent(new BookOrderEvent<>
                                    (relevantOrder.getBookTitle(), customer.getId(), relevantOrder.getTick())));
        });
    }
}
