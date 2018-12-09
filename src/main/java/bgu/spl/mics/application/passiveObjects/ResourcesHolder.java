package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private static ResourcesHolder theSingleton = null;
    private Semaphore semaphore;
    private Queue<DeliveryVehicle> availableVehicles;

    private ResourcesHolder() {
    }

    public static ResourcesHolder getInstance() {
        if (ResourcesHolder.theSingleton == null) {
            ResourcesHolder.theSingleton = new ResourcesHolder();
        }
        return ResourcesHolder.theSingleton;
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
        if (semaphore.tryAcquire()) {
            future.resolve(availableVehicles.poll());
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
        availableVehicles.add(vehicle);
        semaphore.release();
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
