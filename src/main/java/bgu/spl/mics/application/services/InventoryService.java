package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.PoisonPill;
import bgu.spl.mics.application.messages.TakeFromInventoryEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

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
	private CountDownLatch countDownLatch;

	public InventoryService(CountDownLatch countDownLatch, int seq) {
		super("InventoryService " + seq);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(TakeFromInventoryEvent.class, this::processTake);
		subscribeEvent(CheckAvailabilityEvent.class, this::processCheckAv);
		subscribeBroadcast(PoisonPill.class, poison -> terminate());
		countDownLatch.countDown();
	}

	private void processTake(TakeFromInventoryEvent takeEvent){
		if (BookStoreRunner.debug){System.out.println("inside InventoryService.processTake()");}
		OrderResult orderResult = inventory.take(takeEvent.getBookName());
	    complete(takeEvent, orderResult);
    }


    private void processCheckAv(CheckAvailabilityEvent checkEvent){
		if (BookStoreRunner.debug){System.out.println("inside InventoryService.processCheckAv()");}
	    Integer price = inventory.checkAvailabiltyAndGetPrice(checkEvent.getBookName());
	    complete(checkEvent, price);
    }

}
