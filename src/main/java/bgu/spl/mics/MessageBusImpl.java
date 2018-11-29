package bgu.spl.mics;

import bgu.spl.mics.application.ServicePool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private Map<String, ServicePool> eventTypeToServicePool;
    private Map<String, List<ServicePool>> broadcastTypeToServicePools;
    private Map<String, ServicePool> serviceTypeToPool;


    private static MessageBusImpl theSingleton = null;

    private MessageBusImpl() {
        eventTypeToServicePool = new ConcurrentHashMap<>();
        serviceTypeToPool = new ConcurrentHashMap<>();
        broadcastTypeToServicePools = new ConcurrentHashMap<>();
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

        ServicePool correctPool = serviceTypeToPool.get(type.getSimpleName());
        correctPool = instansiatePoolIfAbcent(m, correctPool);
        correctPool.addIfAbcent(m);
        serviceTypeToPool.put(m.getClass().getSimpleName(), correctPool);

        eventTypeToServicePool.put(type.getSimpleName(), correctPool);

        register(m);

    }

    private ServicePool instansiatePoolIfAbcent(MicroService m, ServicePool pool) {
        if (pool == null) {
            pool = new ServicePool();
            serviceTypeToPool.put(m.getClass().getSimpleName(), pool);
        }
        return pool;
    }


    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        ServicePool correctPool = serviceTypeToPool.get(m.getClass().getSimpleName());
        List<ServicePool> subscribedPools = broadcastTypeToServicePools.get(type.getSimpleName());
        subscribedPools = instanciatePoolsListIfAbcent(type, subscribedPools);

        if (!subscribedPools.contains(correctPool)){
            subscribedPools.add(correctPool);
        }

    }

    private List<ServicePool> instanciatePoolsListIfAbcent(Class<? extends Broadcast> type, List<ServicePool> broadcastPools) {
        if (broadcastPools == null) {
            broadcastPools = new ArrayList<>();
            broadcastTypeToServicePools.put(type.getSimpleName(), broadcastPools);
        }
        return broadcastPools;
    }

    @Override
    public <T> void complete(Event<T> e, T result) {


    }

    @Override
    public void sendBroadcast(Broadcast b) {
        String broadcastType = b.getClass().getSimpleName();
        List<ServicePool> broadcastPools = broadcastTypeToServicePools.get(broadcastType);
        for (ServicePool pool: broadcastPools) {
            pool.addToEveryonesQueue(b);
        }
    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		String eventType = e.getClass().getSimpleName();
        ServicePool pool = eventTypeToServicePool.get(eventType);
        pool.addToNextRobinQueue(e);
		return future;
	}

    @Override
	public void register(MicroService m) {
        ServicePool pool = serviceTypeToPool.get(m.getClass().getSimpleName());
        pool.register(m);

    }

    @Override
    public void unregister(MicroService m) {
        ServicePool pool = serviceTypeToPool.get(m.getClass().getSimpleName());
        pool.unregister(m);

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }


}
