package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.ConcurrentLinkedDeque;

public class CircularQueue<T> {
    private ConcurrentLinkedDeque<T> dQueue;

    public CircularQueue() {
        this.dQueue = new ConcurrentLinkedDeque<>();
    }

    public void add(T obj){
        this.dQueue.addFirst(obj);
    }

    public T get(){
        T result = this.dQueue.removeFirst();
        this.dQueue.addLast(result);
        return result;
    }
}
