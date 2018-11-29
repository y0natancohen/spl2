package bgu.spl.mics.application;

import bgu.spl.mics.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServicePool{
    MessageBusImpl messageBus = MessageBusImpl.getInstance();
    private List<MicroService> services;
    private Map<MicroService, Queue<Message>> serviceToQueue;
    private int nextServiceToUse = 0;

    public ServicePool() {
        this.services = new ArrayList<>();
        serviceToQueue = new ConcurrentHashMap<>();

    }

    public List<MicroService> getServices(){
        return services;
    }

    public void addToEveryonesQueue(Broadcast broadcast){
        for (Queue<Message> queue: serviceToQueue.values()){
            queue.add(broadcast);
        }
    }

    public void addToNextRobinQueue(Event event){
        MicroService service = getRobinService();
        Queue<Message> queue = serviceToQueue.get(service);
        queue.add(event);
    }

    public boolean isEmpty(){
        return services.isEmpty();
    }

    public void addIfAbcent(MicroService service){
        if (!services.contains(service))
        this.services.add(service);
    }

    public void remove(MicroService service){
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i) == service) {
                services.remove(i);
                break;
            }
        }
    }

    public MicroService getRobinService(){
        synchronized (messageBus){
            MicroService service = services.get(nextServiceToUse % services.size());
            nextServiceToUse = (nextServiceToUse + 1) % services.size();
            return service;
        }
    }

    public void register(MicroService service){
        serviceToQueue.put(service, new LinkedList<>());
    }

    public void unregister(MicroService service){
        services.remove(service);
        serviceToQueue.remove(service);
    }

}
