package bgu.spl.mics.application.messages;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class AcquireVehicleEvent extends FuturedEvent<Future<DeliveryVehicle>> {

    public AcquireVehicleEvent() {
    }
}
