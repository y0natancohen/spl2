package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can addIfAbcent ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

    /**
     * Retrieves the single instance of this class.
     */
    private Semaphore semaphore;
    private Queue<DeliveryVehicle> availableVehicles;
    private Queue<Future<DeliveryVehicle>> waitingFutures;
    private final Object waitingsLock;

    private ResourcesHolder() {
        this.waitingFutures = new ConcurrentLinkedQueue<>();
        this.waitingsLock = new Object();
    }

    private static class SingletonHolder {
        private static ResourcesHolder instance = new ResourcesHolder();
    }

    public static ResourcesHolder getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     *
     * @return {@link Future<DeliveryVehicle>} object which will resolve to a
     * {@link DeliveryVehicle} when completed.
     */
    public Future<DeliveryVehicle> acquireVehicle() {
        Future<DeliveryVehicle> future = new Future<>();
        // sync for this scenario: vehicle is released together exactly on acquiring
        // if release occurred before adding vehicle to waitingFutures this future wont be resolved
        synchronized (waitingsLock) {
            if (semaphore.tryAcquire()) {
                future.resolve(availableVehicles.poll());
            } else {
                waitingFutures.add(future);
            }
        }
        return future;
    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     *
     * @param vehicle {@link DeliveryVehicle} to be released.
     */
    public void releaseVehicle(DeliveryVehicle vehicle) {
        // sync for this scenario: vehicle is released together exactly on acquiring
        // if acquiring thread just missed the current release it wont be resolved hence - synched
        synchronized (waitingsLock) {
            boolean waitingQEmpty = CollectionUtils.isEmpty(waitingFutures);
            if (waitingQEmpty) {
                availableVehicles.add(vehicle);
                semaphore.release();
            } else {
                Future<DeliveryVehicle> deliveryVehicleFuture = waitingFutures.poll();
                if (deliveryVehicleFuture != null) {
                    deliveryVehicleFuture.resolve(vehicle);
                }
            }
        }
    }

    /**
     * Receives a collection of availableVehicles and stores them.
     * <p>
     *
     * @param vehicles Array of {@link DeliveryVehicle} instances to store.
     */
    public void load(DeliveryVehicle[] vehicles) {
        this.semaphore = new Semaphore(vehicles.length, true);
        this.availableVehicles = new ConcurrentLinkedQueue<>(Arrays.asList(vehicles));
    }

}
