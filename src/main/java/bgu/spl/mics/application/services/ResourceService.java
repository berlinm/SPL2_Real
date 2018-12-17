package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.InviteDriverEvent;
import bgu.spl.mics.Messages.ReleaseVhicleEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link //ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link// MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	ResourcesHolder resourcesHolder;

	public ResourceService(String name) {
		super(name);
		resourcesHolder = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		subscribeEvent(ReleaseVhicleEvent.class,ev->{
			this.resourcesHolder.releaseVehicle(ev.getMydev());
			complete(ev,true);
				});
		subscribeEvent(InviteDriverEvent.class,ev->{
			System.out.println(getName() + " got InviteDriverEvent");
			Future<DeliveryVehicle> result=resourcesHolder.acquireVehicle();
			complete(ev,result.get());
			System.out.println(getName() + " finished executing InviteDriverEvent");
		});
		subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>(){
			@Override
			public void call(TerminationBroadcast c){
				System.out.println(getName() + " got Termination Broadcast");
				unregister();
				terminate();
				System.out.println(getName() + " Terminated");
			}
		});
		System.out.println(this.getName() + " Initialization ended");
	}

}
