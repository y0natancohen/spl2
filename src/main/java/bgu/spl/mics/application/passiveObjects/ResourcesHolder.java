package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private Queue<DeliveryVehicle> vehicles;
	private Map<DeliveryVehicle, AtomicBoolean> vehicleToUsed;

	private ResourcesHolder() {
	    this.vehicles = new ConcurrentLinkedDeque<>();
    }

	public static ResourcesHolder getInstance(){
		if (ResourcesHolder.theSingleton == null){
			ResourcesHolder.theSingleton = new ResourcesHolder();
		}
		return ResourcesHolder.theSingleton;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		//todo elad and jony think wtf is here
	    Future<DeliveryVehicle> future = new Future<>();
		return null;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		//TODO: Implement this
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles = new ConcurrentLinkedDeque<>(Arrays.asList(vehicles));
	}

}
