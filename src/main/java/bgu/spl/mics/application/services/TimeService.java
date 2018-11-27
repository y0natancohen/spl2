package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private static TimeService theSingleton = null;
	private static int speed;
	private static int duration;

	public static TimeService getInstance(){
		if (TimeService.theSingleton == null){
			TimeService.theSingleton = new TimeService();
		}
		return TimeService.theSingleton;
	}


	// TODO: public is their signature here
    // TODO: how to prevent from creating another one?
	public TimeService() {
		super("Change_This_Name");
		// TODO Implement this
	}

    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        TimeService.speed = speed;
    }

    public static int getDuration() {
        return duration;
    }

    public static void setDuration(int duration) {
        TimeService.duration = duration;
    }

    @Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
