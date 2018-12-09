package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.Objects;

public class BaseEvent<T> implements Event<T> {
    private Future<T> future;

    public Future<T> getFuture() {
        return future;
    }

    public void setFuture(Future<T> future) {
        this.future = future;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEvent<?> baseEvent = (BaseEvent<?>) o;
        return Objects.equals(future, baseEvent.future);
    }

    @Override
    public int hashCode() {

        return Objects.hash(future);
    }
}
