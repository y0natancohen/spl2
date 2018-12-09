package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.Objects;

public class TickBroadcast implements Broadcast {
    int currentTick;

    public TickBroadcast(int currentTick) {
        this.currentTick = currentTick;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickBroadcast that = (TickBroadcast) o;
        return currentTick == that.currentTick;
    }

    @Override
    public int hashCode() {

        return Objects.hash(currentTick);
    }
}
