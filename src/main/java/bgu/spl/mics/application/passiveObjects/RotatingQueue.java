package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.ConcurrentLinkedDeque;

public class RotatingQueue<E> extends ConcurrentLinkedDeque<E>{

    public E getAndRotate(){
        //todo couldnt find a better way then sync... convert to cas maybe?
        synchronized (this){
            E result = removeFirst();
            if (result != null){
                addLast(result);
                return result;
            }else return null;
        }
    }
}
