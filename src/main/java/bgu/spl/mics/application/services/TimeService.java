package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can addIfAbcent private fields and public methods to this class.
 * You MAY change constructor signatures and even addIfAbcent new public constructors.
 */
public class TimeService extends MicroService {
    private static TimeService theSingleton;
    private int speed;
    private int duration;

    public static TimeService getInstance() {
        if (theSingleton == null) {
            theSingleton = new TimeService();
        }
        return theSingleton;
    }

    private TimeService() {
        super("Change_This_Name");
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        // TODO Implement this

    }

}
