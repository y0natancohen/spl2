package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

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

	public LogisticsService() {
		super("LogisticsService");
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, this::processDelivery);
	}

	private void processDelivery(DeliveryEvent deliveryEvent){
		DeliveryVehicle vehicle = sendEvent(new AcquireVehicleEvent()).get();
		vehicle.deliver(deliveryEvent.getAddress(), deliveryEvent.getDistance());
		sendEvent(new ReleaseVehicleEvent(vehicle));
		complete(deliveryEvent, true);
	}

}
