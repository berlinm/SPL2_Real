package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link //BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private MoneyRegister moneyRegister;
	int orders;
	public SellingService(String name) {
		super(name);
		moneyRegister = MoneyRegister.getInstance();
		orders=0;
	}

	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		subscribeEvent(BookOrderEvent.class, new Callback<BookOrderEvent>() {
			@Override
			public void call(BookOrderEvent orderEvent) {
				System.out.println(getName() + " got new BookOrderEvent from " + orderEvent.getSenderName() + "(Book: " + orderEvent.getOrderedBook() + ", ordered tick:" + orderEvent.getOrderTick() + ")");
				int proccessingTick = sendEvent(new AskForTickEvent()).get().intValue();
				//lets get price (-1 if absent)
				CheckInventoryEvent invEvent = new CheckInventoryEvent(orderEvent.getOrderedBook(), getName());
				Future<AtomicInteger> checkInventoryResult = sendEvent(invEvent);
				AtomicInteger atomicPrice = checkInventoryResult.get();
				System.out.println(atomicPrice.intValue());
				if (atomicPrice.intValue() < 0){
					System.out.println("Could not get books price");
					complete(orderEvent, null);
					return;
				}
				//lets charge the customer
				try {
					moneyRegister.chargeCreditCard(orderEvent.getCustomer(), atomicPrice.intValue());
				} catch (Exception e) {
					//Customer has no money for this book
					BookSemaphoreHolder.getInstance().release(orderEvent.getOrderedBook());
					complete(orderEvent, null);
					return;
				}
				//lets take book
				Future<Boolean> takeRes = sendEvent(new TakeBookEvent(orderEvent.getOrderedBook(), getName()));
				Boolean isTakeBookSucceeded = takeRes.get();
				//if the future result is null - there are not a service available for the request - HAZLESH!
				if (isTakeBookSucceeded == null){
					BookSemaphoreHolder.getInstance().release(orderEvent.getOrderedBook());
					complete(orderEvent, null);
					return;
				}
				if (!isTakeBookSucceeded){
					throw new BookNotInInventoryException("Something wrong. selling service " + getName() + "tried to order "
					+ orderEvent.getOrderedBook() + "And the price was not -1 even though book was not in stock or not exists.");
				}
				//lets get the current tick for the issued tick
				Future<AtomicInteger> issuedTick = sendEvent(new AskForTickEvent());
				if (isTakeBookSucceeded == null){
					BookSemaphoreHolder.getInstance().release(orderEvent.getOrderedBook());
					throw new TimerServiceDoesnNotExistException("Ask for tick resolved as null. TimeService does not exist?");
				}
				OrderReceipt orderReceipt = new OrderReceipt(orders, getName(), orderEvent.getCustomer().getId(), orderEvent.getOrderedBook(), checkInventoryResult.get().intValue(), proccessingTick , orderEvent.getOrderTick(), issuedTick.get().intValue());
				Future<Boolean> deliveryRes = null;
				do {
					DeliveryEvent deliveryEvent = new DeliveryEvent(orderEvent.getCustomer(), getName());
					deliveryRes = sendEvent(deliveryEvent);
					System.out.println("if this shows up a lot of times than we have an infinite loop");
				} while (!deliveryRes.get());
				System.out.println(getName() + " finished executing BookOrderEvent from " + orderEvent.getSenderName() + "(Book: " + orderEvent.getOrderedBook() + ", ordered tick:" + orderEvent.getOrderTick() + ")");
				complete(orderEvent, orderReceipt);
			}
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
	private class BookNotInInventoryException extends RuntimeException{
		private String doc;
		public BookNotInInventoryException(){this.doc = "";}
		public BookNotInInventoryException(String doc){
			this.doc = doc;
			System.out.println(this.doc);
		}

		public String getDoc() {
			return doc;
		}
	}
	private class TimerServiceDoesnNotExistException extends RuntimeException{
		private String doc;
		public TimerServiceDoesnNotExistException(){this.doc = "";}
		public TimerServiceDoesnNotExistException(String doc){
			this.doc = doc;
		}

		public String getDoc() {
			return doc;
		}
	}
}