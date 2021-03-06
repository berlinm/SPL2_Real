package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.InviteDriverEvent;
import bgu.spl.mics.Messages.ReleaseVhicleEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		subscribeEvent(DeliveryEvent.class,ev->{
			System.out.println(getName() + " got DeliveryEvent from " + ev.getSenderName() +  "( Customer: " + ev.getCustomer().getName() + ")");
			InviteDriverEvent IDE=new InviteDriverEvent(this.getName());
			//now we got myDelivery that is the result of the InviteDriverEvent (that will be resolved to a future,
			//when resource service will end its run
			Future<Future<DeliveryVehicle>> myDelivery=sendEvent(IDE);
			//now we got mdv which is a future (that we got from resource holder) that
			//when one will be free, will resolve to a vehicle
			Future<DeliveryVehicle> mdv=myDelivery.get();
			DeliveryVehicle deliveryVehicle = mdv.get();
			deliveryVehicle.deliver(ev.getCustomer().getAddress(),ev.getCustomer().getDistance());
			ReleaseVhicleEvent releaseVhicleEvent=new ReleaseVhicleEvent(deliveryVehicle);
			sendEvent(releaseVhicleEvent);
			complete(ev, true);
			System.out.println(getName() + " finished executing DeliveryEvent from " + ev.getSenderName() +  "( Customer: " + ev.getCustomer().getName() + ")");
		});
		subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>(){
			@Override
			public void call(TerminationBroadcast c){
				System.out.println(getName() + " got Termination Broadcast");
				terminate();
				unregister();
				System.out.println(getName() + " Terminated");
			}
		});
		System.out.println(this.getName() + " Initialization ended");

	}

}
