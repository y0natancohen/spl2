package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.PoisonPill;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch;

	public LogisticsService(CountDownLatch countDownLatch, int seq) {
		super("LogisticsService " + seq);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, this::processDelivery);
		subscribeBroadcast(PoisonPill.class, poison -> terminate());
		countDownLatch.countDown();
	}

	private void processDelivery(DeliveryEvent deliveryEvent){
		Future<DeliveryVehicle> deliveryVehicleFuture = sendEvent(new AcquireVehicleEvent()).get();
		if (deliveryVehicleFuture == null){  // we are in system shutdown, was resolved with null
            complete(deliveryEvent, false);
        }else{
            DeliveryVehicle vehicle = deliveryVehicleFuture.get();
            if (vehicle != null){
				String address = deliveryEvent.getAddress();
				int distance = deliveryEvent.getDistance();
				vehicle.deliver(address, distance);
				sendEvent(new ReleaseVehicleEvent(vehicle));
			}
            complete(deliveryEvent, true);
        }
	}
}
