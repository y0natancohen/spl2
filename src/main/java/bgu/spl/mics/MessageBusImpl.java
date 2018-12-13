package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.RotatingQueue;
import bgu.spl.mics.application.services.TimeService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


    private Map<Class, RotatingQueue<MicroService>> eventTypeToServices;
    private Map<String, BlockingQueue<Message>> serviceToQueue;
    private Map<Class, Queue<MicroService>> broadcastToServices;


    private MessageBusImpl() {
        eventTypeToServices = new ConcurrentHashMap<>();
        serviceToQueue = new ConcurrentHashMap<>();
        broadcastToServices = new ConcurrentHashMap<>();
    }

    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

        if (eventTypeToServices.get(type) == null) {
            synchronized (this) {
                if (eventTypeToServices.get(type) == null) {
                    eventTypeToServices.put(type, new RotatingQueue<>());
                }
            }
        }
        eventTypeToServices.get(type).add(m);
    }


    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (broadcastToServices.get(type) == null) {
            synchronized (this) {
                if (broadcastToServices.get(type) == null) {
                    broadcastToServices.put(type, new ConcurrentLinkedQueue<>());
                }
            }
        }
        broadcastToServices.get(type).add(m);
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
        Queue<MicroService> services = broadcastToServices.get(b.getClass());
        if (services != null){ // otherwise no one cares about this broadcast
            services.forEach(microService -> {
                BlockingQueue<Message> currentQ = serviceToQueue.get(microService.getName());
                try {
                    currentQ.put(b);
                } catch (InterruptedException e) {
                    System.out.println("!!!!! was interupted while waiting for put in broadcast queue!!!");
                }
            });
        }

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        RotatingQueue<MicroService> services = eventTypeToServices.get(e.getClass());
        Future<T> future = new Future<>();
        e.setFuture(future);
        if (services == null){ // no one cares about this event, so no result as well
            future.resolve(null);
        }else{
            MicroService service = services.getAndRotate();
            BlockingQueue<Message> events = serviceToQueue.get(service.getName());
            try {
                events.put(e);
            } catch (InterruptedException e1) {
                System.out.println("!!!!! was interupted while waiting to put in event queue!!!");
            }
        }

        return future;
    }

    @Override
    public void register(MicroService m) {
        serviceToQueue.put(m.getName(), new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        BlockingQueue<Message> q = serviceToQueue.get(m.getName());
        for (Message message : q){
            if (message instanceof Event){
                Event e = (Event) message;
                e.getFuture().resolve(null);
            }
        }
        serviceToQueue.remove(m.getName());
        removeFromValues(eventTypeToServices, m);
        removeFromValues(broadcastToServices, m);
    }

    private void removeFromValues(Map<Class, ? extends Collection> map, MicroService m) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Collection<MicroService> services = (Collection) pair.getValue();
            for (MicroService service : services) {
                if (service == m) {
                    services.remove(service);
                    break;
                }

            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> serviceQ = serviceToQueue.get(m.getName());
        return serviceQ.take();
    }


}
