package bgu.spl.mics.application.messages;

public class DeliveryEvent extends BaseEvent<Boolean> {
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
}
