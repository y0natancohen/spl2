package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class ResourceService extends MicroService {
    private static ResourcesHolder resources = ResourcesHolder.getInstance();

    public ResourceService() {
        super("ResourceService");
    }

    @Override
    protected void initialize() {
        subscribeEvent(AcquireVehicleEvent.class, this::aquire);
        subscribeEvent(ReleaseVehicleEvent.class, this::release);
    }

    private void release(ReleaseVehicleEvent releaseEvent) {
        resources.releaseVehicle(releaseEvent.getVehicle());
        complete(releaseEvent, true);
    }

    private void aquire(AcquireVehicleEvent aquireEvent) {
        Future<DeliveryVehicle> deliveryVehicleFuture = resources.acquireVehicle();
        complete(aquireEvent, deliveryVehicleFuture.get());
    }

}
