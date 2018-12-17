package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.ConcurrentLinkedDeque;

public class RotatingQueue<E> extends ConcurrentLinkedDeque<E> {

    public E getAndRotate() {
        // synch rotation to support round robin even
        // if service has unregistered within this time
        synchronized (this) {
            E result = removeFirst();
            if (result != null) {
                addLast(result);
                return result;
            } else return null;
        }
    }
}
