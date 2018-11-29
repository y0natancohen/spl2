package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class ServicePool{
    MessageBusImpl messageBus = MessageBusImpl.getInstance();
    private List<MicroService> services;
    private int nextServiceToUse = 0;
    private Map<MicroService, Queue> serviceToQueue;

    public ServicePool() {
        this.services = new ArrayList<>();
        serviceToQueue = new ConcurrentHashMap<>();

    }

    public boolean isEmpty(){
        return services.isEmpty();
    }

    public void add(MicroService service){
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
            MicroService service = services.get(nextServiceToUse);
            nextServiceToUse = (nextServiceToUse + 1) % services.size();
            return service;
        }
    }

}
