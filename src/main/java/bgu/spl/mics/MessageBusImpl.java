package bgu.spl.mics;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


    private Map<Class, List<MicroService>> eventTypeToServices;
    private Map<String, BlockingQueue<Message>> serviceToQueue;
    private Map<Class, List<MicroService>> broadcastToServices;


    private static MessageBusImpl theSingleton = null;

    private MessageBusImpl() {
        eventTypeToServices = new ConcurrentHashMap<>();
        serviceToQueue = new ConcurrentHashMap<>();
        broadcastToServices = new ConcurrentHashMap<>();
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
        List<MicroService> microServices = eventTypeToServices.get(type.getClass());
        if (CollectionUtils.isEmpty(microServices)) {
            microServices = new ArrayList<>();
            microServices.add(m);
        } else {
            microServices.add(m);
        }
        eventTypeToServices.put(type.getClass(), microServices);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        List<MicroService> microServices = broadcastToServices.get(type.getClass());
        if (CollectionUtils.isEmpty(microServices)) {
            microServices = new ArrayList<>();
            microServices.add(m);
        } else {
            microServices.add(m);
        }
        broadcastToServices.put(type.getClass(), microServices);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> futureToResolve = e.getFuture();
        if (futureToResolve != null) {
            futureToResolve.resolve(result);
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        broadcastToServices.get(b).forEach(microService -> {
            BlockingQueue<Message> currentQ = serviceToQueue.get(microService.getName());
            try {
                currentQ.put(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        List<MicroService> services = eventTypeToServices.get(e.getClass());
        // TODO pick service in round robin by event type
        BlockingQueue<Message> events = serviceToQueue.get(services.get(0));
        try {
            events.put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Future<T> future = new Future<>();
        e.setFuture(future);
        return future;
    }

    @Override
    public void register(MicroService m) {
        // TODO need to find unique identifier for service
        serviceToQueue.put(m.getName(), new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        serviceToQueue.remove(m.getName());
        // todo clear from subscription
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> serviceQ = serviceToQueue.get(m.getName());
        return serviceQ.take();
    }


}
