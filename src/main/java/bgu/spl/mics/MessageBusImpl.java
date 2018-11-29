package bgu.spl.mics;

import bgu.spl.mics.application.ServicePool;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private Map<String, ServicePool> eventTypeToServicePool;
    private Map<String, ServicePool> broadcastTypeToServicePool;


    private static MessageBusImpl theSingleton = null;

    private MessageBusImpl() {
        eventTypeToServicePool = new ConcurrentHashMap<>();
//        serviceTypeToServices = new ConcurrentHashMap<>();
        broadcastTypeToServicePool = new ConcurrentHashMap<>();
//        serviceToQueue = new ConcurrentHashMap<>();
//        eventTypeToRobinIndex = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (MessageBusImpl.theSingleton == null) {
            MessageBusImpl.theSingleton = new MessageBusImpl();
        }
        return MessageBusImpl.theSingleton;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        //todo is sync?

        ServicePool pool = eventTypeToServicePool.get(type.getSimpleName());
        if (pool == null) {
            pool = new ServicePool();
            eventTypeToServicePool.put(type.getSimpleName(), pool);
        }
        pool.add(m);

        eventTypeToServicePool.put(type.getSimpleName(), pool);
//        serviceTypeToServices.put(m.getClass().getSimpleName(), microServices);

        register(m);

    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        List<MicroService> microServices = broadcastTypeToServicePool.get(type.getSimpleName());
        if (CollectionUtils.isEmpty(microServices)) {
            microServices = new ArrayList<>();
            microServices.add(m);
        } else {
            microServices.add(m);
        }
        broadcastTypeToServicePool.put(type.getSimpleName(), microServices);

    }

    @Override
    public <T> void complete(Event<T> e, T result) {


    }

    @Override
    public void sendBroadcast(Broadcast b) {
        String broadcastType = b.getClass().getSimpleName();
        List<MicroService> services = broadcastTypeToServicePool.get(broadcastType);
        for (MicroService service: services) {
            Queue queue = serviceToQueue.get(service);
            queue.add(b);
        }
    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		String eventType = e.getClass().getSimpleName();
        List<MicroService> services = eventTypeToServicePool.get(eventType);
        MicroService service = getNextRobinService(services, eventType);
        Queue queue = serviceToQueue.get(service);
        queue.add(e);
		return future;
	}

    private MicroService getNextRobinService(List<MicroService> services, String eventType) {
        synchronized (theSingleton){
            int index = eventTypeToRobinIndex.get(eventType);
            eventTypeToRobinIndex.put(eventType, (index + 1) % services.size());
            return services.get(index);
        }
    }

    @Override
	public void register(MicroService m) {
        serviceToQueue.put(m, new LinkedList());

    }

    @Override
    public void unregister(MicroService m) {
        serviceToQueue.remove(m);
        List<MicroService> services = serviceTypeToServices.get(m.getClass().getSimpleName());

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }


}
