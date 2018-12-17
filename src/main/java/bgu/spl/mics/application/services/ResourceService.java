package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.PoisonPill;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

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
    private CountDownLatch countDownLatch;
    private static ResourcesHolder resources = ResourcesHolder.getInstance();
    private Queue<Future> futuresLog;

    public ResourceService(CountDownLatch countDownLatch, int seq) {
        super("ResourceService " + seq);
        this.countDownLatch = countDownLatch;
        futuresLog = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void initialize() {
        subscribeEvent(AcquireVehicleEvent.class, this::aquire);
        subscribeEvent(ReleaseVehicleEvent.class, this::release);
        subscribeBroadcast(PoisonPill.class, poison -> {
                    terminate();
                    destroyTheDeliveryFuture();
                }
        );
        countDownLatch.countDown();
    }

    private void destroyTheDeliveryFuture() {
        for (Future future : futuresLog){
            future.resolve(null);
        }
    }

    private void release(ReleaseVehicleEvent releaseEvent) {
        resources.releaseVehicle(releaseEvent.getVehicle());
        complete(releaseEvent, true);
    }

    private void aquire(AcquireVehicleEvent aquireEvent) {
        Future<DeliveryVehicle> deliveryVehicleFuture = resources.acquireVehicle();
        futuresLog.add(deliveryVehicleFuture);
        complete(aquireEvent, deliveryVehicleFuture);
    }

}
