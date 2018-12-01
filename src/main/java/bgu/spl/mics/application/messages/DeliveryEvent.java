package bgu.spl.mics.application.messages;

public class DeliveryEvent extends BaseEvent<Boolean> {
    private String address;

    public String getAddress() {
        return address;
    }

    public DeliveryEvent(String address) {
        this.address = address;
    }
}
