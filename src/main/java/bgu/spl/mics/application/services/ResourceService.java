package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class ResourceService extends MicroService{
	private static ResourcesHolder resources = ResourcesHolder.getInstance();

	public ResourceService() {
		super("ResourceService");
	}

	@Override
	protected void initialize() {
	    subscribeEvent(AquireVehicleEvent.class, this::aquire);
	    subscribeEvent(ReleaseVehicleEvent.class, this::release);
	}

    private void release(ReleaseVehicleEvent releaseEvent) {
	    resources.releaseVehicle(releaseEvent.getVehicle());
	    complete(releaseEvent, true);
    }

    private void aquire(AquireVehicleEvent aquireEvent){
	     DeliveryVehicle vehicle = resources.acquireVehicle().get();
	     complete(aquireEvent, vehicle);
    }

}
