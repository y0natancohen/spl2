package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;

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
    private int speed;
    private int duration;
    private int tickCount = 1;

    private static class SingletonHolder {
        private static MicroService instance = new TimeService();
    }

    public static MicroService getInstance() {
        return SingletonHolder.instance;
    }

    private TimeService() {
        super("TimeService");
    }

    @Override
    protected void initialize() {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                if (tickCount < duration) {
                    tickCount = tickCount++;
                    System.out.println("sending tick" + tickCount);
                    sendBroadcast(new TickBroadcast(tickCount));
                }
                terminate();
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, speed);
    }
}
