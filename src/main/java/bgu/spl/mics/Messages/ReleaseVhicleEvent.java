package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVhicleEvent implements Event<Boolean> {

    private DeliveryVehicle mydev;

    public ReleaseVhicleEvent (DeliveryVehicle mydev){
        this.mydev=mydev;
    }

    public DeliveryVehicle getMydev() {
        return mydev;
    }
}
