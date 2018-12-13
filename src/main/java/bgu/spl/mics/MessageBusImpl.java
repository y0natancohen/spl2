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
    private Map<Class, RotatingQueue<MicroService>> broadcastToServices;
    private Queue<Future> futuresLog;


    private MessageBusImpl() {
        eventTypeToServices = new ConcurrentHashMap<>();
        serviceToQueue = new ConcurrentHashMap<>();
        broadcastToServices = new ConcurrentHashMap<>();
        futuresLog = new ConcurrentLinkedQueue<>();
    }

    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        RotatingQueue<MicroService> services = eventTypeToServices.get(type);
        if (CollectionUtils.isEmpty(services)) {
            services = new RotatingQueue<>();
            services.addFirst(m);
        } else {
            services.addFirst(m);
        }
        eventTypeToServices.put(type, services);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        RotatingQueue<MicroService> microServices = broadcastToServices.get(type);
        if (CollectionUtils.isEmpty(microServices)) {
            microServices = new RotatingQueue<>();
            microServices.addFirst(m);
        } else {
            microServices.addFirst(m);
        }
        broadcastToServices.put(type, microServices);
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
        broadcastToServices.get(b.getClass()).forEach(microService -> {
            BlockingQueue<Message> currentQ = serviceToQueue.get(microService.getName());
            try {
                currentQ.put(b);

//                System.out.println("--------------------------------------");
//                System.out.println(serviceToQueue.toString());
//                System.out.println("--------------------------------------");
            } catch (InterruptedException e) {
                System.out.println("!!!!! was interupted while waiting for put in broadcast queue!!!");
            }
        });
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        RotatingQueue<MicroService> services = eventTypeToServices.get(e.getClass());
        MicroService service = services.getAndRotate();
        BlockingQueue<Message> events = serviceToQueue.get(service.getName());
        Future<T> future = new Future<>();
        futuresLog.add(future);
        e.setFuture(future);
        try {
            events.put(e);
        } catch (InterruptedException e1) {
            System.out.println("!!!!! was interupted while waiting to put in event queue!!!");
        }
        return future;
    }

    @Override
    public void register(MicroService m) {
        serviceToQueue.put(m.getName(), new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        if (!(m instanceof TimeService)) {
            destroyTheFuture();
        }
        serviceToQueue.remove(m.getName());
        removeFromValues(eventTypeToServices, m);
        removeFromValues(broadcastToServices, m);
    }

    private void destroyTheFuture() {
//        System.out.println("im destroying the future");
        for (Future future: futuresLog){
            future.resolve(null);
        }
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
