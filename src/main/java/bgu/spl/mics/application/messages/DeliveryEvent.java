package bgu.spl.mics.application.messages;

import java.util.Objects;

public class DeliveryEvent extends FuturedEvent<Boolean> {
    private String address;
    private int distance;

    public int getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public DeliveryEvent(String address, int distance) {
        this.address = address;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryEvent that = (DeliveryEvent) o;
        return distance == that.distance &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(address, distance);
    }
}
