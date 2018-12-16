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
	protected ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> orderSchedule;
	private Customer customer;
	private List<Future<OrderReceipt>> receiptFuture;
	public APIService(String name,ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> orderSchedule,Customer customer) {
		super(name);
		if(orderSchedule.isEmpty()){
			throw new IllegalArgumentException("Orders Schedule can't be empty ");
		}
		this.orderSchedule = orderSchedule;
		this.customer=customer;
		receiptFuture=new LinkedList<>();
	}

	public ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> getOrderSchedule() {
		return orderSchedule;
	}
	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		Customer customer=this.customer;
		subscribeBroadcast(TickBroadcast.class, c -> {
			System.out.println(getName() + " got Tick broadcast, Curr tick: " + c.getCurrentTick());
			for (AtomicInteger a: orderSchedule.keySet()) {
				if (a.intValue() == c.getCurrentTick()){
					for (BookOrderEvent bookOrderEvent : this.orderSchedule.get(a)) {
						Future<OrderReceipt> result = sendEvent(bookOrderEvent);
						receiptFuture.add(result);
					}
				}
			}
			System.out.println(getName() + " finished Tick broadcast execution, Curr tick: "+c.getCurrentTick());
		});
		subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>(){
			@Override
			public void call(TerminationBroadcast c){
				System.out.println(getName() + " got Termination Broadcast");
				for(int i=0;i<receiptFuture.size();i++){
					if(receiptFuture.get(i).isDone()) {
						customer.getCustomerReceiptList().add(receiptFuture.get(i).get());
					}
				}
				unregister();
				terminate();
				System.out.println(getName() + "Terminated");
			}
		});
		System.out.println(getName() + " Initialization ended");
	}
}
