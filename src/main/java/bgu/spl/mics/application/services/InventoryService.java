package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeFromInventoryEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */

public class InventoryService extends MicroService{

    private static Inventory inventory = Inventory.getInstance();

	public InventoryService() {
		super("InventoryService");
	}

	@Override
	protected void initialize() {
		subscribeEvent(TakeFromInventoryEvent.class, this::processTake);
		subscribeEvent(CheckAvailabilityEvent.class, this::precessCheck);
	}

	private void processTake(TakeFromInventoryEvent takeEvent){
	    OrderResult orderResult = inventory.take(takeEvent.getBookName());
	    complete(takeEvent, orderResult);
    }


    private void precessCheck(CheckAvailabilityEvent checkEvent){
	    Integer price = inventory.checkAvailabiltyAndGetPrice(checkEvent.getBookName());
	    complete(checkEvent, price);
    }

}
