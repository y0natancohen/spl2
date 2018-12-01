package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast Broadcast}.
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
    //todo - can be simply int?
    private final AtomicInteger tickCount = new AtomicInteger(1);

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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               getTask().run();
                               timer.schedule(getTask(), speed);
                           }
                       }
                , speed);
    }

    private TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (tickCount.get() < duration) {
                    sendBroadcast(new TickBroadcast(tickCount.getAndIncrement()));
                }
                //todo- trigger system termination
            }
        };
    }
}
