package bgu.spl.mics;

import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    Map<String, List<MicroService>> eventTypeToServices;
    Map<String, Queue> serviceToQueue;

    private static MessageBusImpl theSingleton = null;

    private MessageBusImpl() {
        eventTypeToServices = new ConcurrentHashMap<>();
        serviceToQueue = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (MessageBusImpl.theSingleton == null) {
            MessageBusImpl.theSingleton = new MessageBusImpl();
        }
        return MessageBusImpl.theSingleton;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        //todo is synch?
        List<MicroService> microServices = eventTypeToServices.get(type.getSimpleName());
        if (CollectionUtils.isEmpty(microServices)) {
            microServices = new LinkedList<>();
            microServices.add(m);
        } else {
            microServices.add(m);
        }
        eventTypeToServices.put(type.getSimpleName(), microServices);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        //todo same as above

    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        // TODO Auto-generated method stub

    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

    }

    @Override
    public void unregister(MicroService m) {
        // TODO Auto-generated method stub

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }


}
