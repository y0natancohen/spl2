package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent extends BaseEvent<Boolean> {
    private DeliveryVehicle vehicle;

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

    public ReleaseVehicleEvent(DeliveryVehicle vehicle) {
        this.vehicle = vehicle;

    }
}
