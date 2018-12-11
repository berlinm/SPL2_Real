package bgu.spl.mics.application.services;

import bgu.spl.mics.BookOrderEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TickBroadcast;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link //BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	ConcurrentHashMap<AtomicInteger,BookOrderEvent> orderSchedule;

	public APIService(ConcurrentHashMap<AtomicInteger,BookOrderEvent> orderSchedule,String name) {
		super(name);
		if(orderSchedule.isEmpty()){
			throw new IllegalArgumentException("Orders Schedule can't be empty ");
		}
		this.orderSchedule=orderSchedule;

	}

	@Override
	protected void initialize() {
		System.out.println("API Service "+this.getName()+" started");

		subscribeBroadcast(TickBroadcast.class,Message->{
			System.out.println("API Service "+this.getName()+"got broadcast from" + TickBroadcast.class.getName());
			// sendEvent(orderSchedule.get()); //need the time from the timer
			terminate();
		});
		
	}

}
