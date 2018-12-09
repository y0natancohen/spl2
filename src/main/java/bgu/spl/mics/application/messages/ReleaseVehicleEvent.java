package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.Objects;

public class ReleaseVehicleEvent extends BaseEvent<Boolean> {
    private DeliveryVehicle vehicle;

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

    public ReleaseVehicleEvent(DeliveryVehicle vehicle) {
        this.vehicle = vehicle;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReleaseVehicleEvent that = (ReleaseVehicleEvent) o;
        return Objects.equals(vehicle, that.vehicle);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), vehicle);
    }
}
