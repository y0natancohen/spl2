package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may addIfAbcent ONLY private fields and methods to this class.
 */
public class DeliveryVehicle {
    /**
     * Constructor.
     */

    private final int license;
    private final int speed;

    public DeliveryVehicle(int licence, int speed) {
        this.license = licence;
        this.speed = speed;
    }


    /**
     * Retrieves the license of this delivery vehicle.
     */
    public int getLicense() {
        return license;
    }

    /**
     * Retrieves the speed of this vehicle person.
     * <p>
     *
     * @return Number of ticks needed for 1 Km.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Simulates a delivery by sleeping for the amount of time that
     * it takes this vehicle to cover {@code distance} KMs.
     * <p>
     *
     * @param address  The address of the customer.
     * @param distance The distance from the store to the customer.
     */
    public void deliver(String address, int distance) {
        int t = distance / this.speed;
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println("!!!!! was interupted while waiting for vehicle to deliver!!!");
        }
    }
}
