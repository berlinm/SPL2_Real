package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.BookOrderEvent;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.BlockingQueue;
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
	private ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> orderSchedule;
	private Customer customer;
	public APIService(ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> orderSchedule,Customer customer) {
		super("API Service");
		if(orderSchedule.isEmpty()){
			throw new IllegalArgumentException("Orders Schedule can't be empty ");
		}
		this.orderSchedule = orderSchedule;
		this.customer=customer;
	}

	@Override
	protected void initialize() {
		System.out.println("API Service "+this.getName()+" started");
		Customer customer=this.customer;
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				System.out.println("API Service "+getName()+"got broadcast from" + TickBroadcast.class.getName());
				for (BookOrderEvent bookOrderEvent: orderSchedule.get(c.getCurrentTick()))
				{
					Future<OrderReceipt> result =sendEvent(bookOrderEvent);
					if(result!=null) {
						OrderReceipt orderReceipt = result.get();
						customer.getCustomerReceiptList().add(orderReceipt);
						DeliveryEvent deliveryEvent = new DeliveryEvent(customer);
						sendEvent(deliveryEvent);
					}
				}
			}
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
