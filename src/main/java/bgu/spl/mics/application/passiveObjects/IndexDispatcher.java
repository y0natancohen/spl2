package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class IndexDispatcher {
    private static class SingletonHolder {
        private static IndexDispatcher instance = new IndexDispatcher();
    }

    public static IndexDispatcher getInstance() {
        return SingletonHolder.instance;
    }
    private AtomicInteger id;

    private IndexDispatcher() {
        id = new AtomicInteger();
    }

    public int getNextId() {
        return id.getAndIncrement();
    }
}
