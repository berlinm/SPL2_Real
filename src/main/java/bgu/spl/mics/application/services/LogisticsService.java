package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.InviteDriverEvent;
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

		subscribeEvent(DeliveryEvent.class,ev->{

			InviteDriverEvent IDE=new InviteDriverEvent();
			Future<DeliveryVehicle> myDelivery=sendEvent(IDE);
			DeliveryVehicle mdv=myDelivery.get();
			mdv.deliver(ev.getCustomer().getAddress(),ev.getCustomer().getDistance());
			terminate();
		});
		subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>(){
			@Override
			public void call(TerminationBroadcast c){
				unregister();
				terminate();
			}
		});
	}

}
