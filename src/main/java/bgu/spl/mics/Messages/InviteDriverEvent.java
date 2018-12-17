package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class InviteDriverEvent implements Event<Future<DeliveryVehicle>> {
    private String src;
    public InviteDriverEvent(String src){
        this.src = src;
    }
    public String getSrc() {
        return src;
    }
}
