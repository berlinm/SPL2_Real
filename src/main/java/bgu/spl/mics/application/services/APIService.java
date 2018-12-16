package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.BookOrderEvent;
import bgu.spl.mics.Messages.DeliveryEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
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
	private List<Future<OrderReceipt>> receiptFuture;
	public APIService(String name,ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> orderSchedule,Customer customer) {
		super(name);
		if(orderSchedule.isEmpty()){
			throw new IllegalArgumentException("Orders Schedule can't be empty ");
		}
		this.orderSchedule = orderSchedule;
		this.customer=customer;
		receiptFuture=new LinkedList<Future<OrderReceipt>>();
	}

	public ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> getOrderSchedule() {
		return orderSchedule;
	}

	@Override
	protected void initialize() {
		System.out.println(this.getName()+" started");
		Customer customer=this.customer;
		subscribeBroadcast(TickBroadcast.class, c -> {
				System.out.println(getName()+" got broadcast from" + c.getClass().getName()+" Curr tick: "+c.getCurrentTick());

				Enumeration<AtomicInteger> emu=this.getOrderSchedule().keys();
				AtomicInteger Key=null;
				boolean b=false;
				while (emu.hasMoreElements()){
					AtomicInteger next=emu.nextElement();
					if(next.intValue()==c.getCurrentTick()){
						b=true;
						Key=next;
					}
				}

				if(b){
					for (BookOrderEvent bookOrderEvent : orderSchedule.get(Key)) {
						Future<OrderReceipt> result = sendEvent(bookOrderEvent);
						receiptFuture.add(result);
						//if (result != null) {
						//	customer.getCustomerReceiptList().add(orderReceipt);
						//	DeliveryEvent deliveryEvent = new DeliveryEvent(customer);
						//	sendEvent(deliveryEvent);
						//}
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
