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
		subscribeEvent(BookOrderEvent.class, new Callback<BookOrderEvent>() {
			@Override
			public void call(BookOrderEvent orderEvent) {
				System.out.println(getName()+" got new event from " +orderEvent.getClass().getName());
				//lets check the current tick for the proccessing tick
				Future<AtomicInteger> currentTick = sendEvent(new AskForTickEvent());
				AtomicInteger AtomicProccessingTick = currentTick.get();
				//if the future result is null - there are not a service available for the request - HAZLESH!
				if (AtomicProccessingTick == null){
					throw new TimerServiceDoesnNotExistException("Ask for tick resolved as null. TimeService does not exist?");
				}
				int proccessingTick = currentTick.get().intValue();
				//lets get price

				System.out.println("Ready");
				CheckInventoryEvent invEvent = new CheckInventoryEvent(orderEvent.getOrderedBook());
				Future<AtomicInteger> inventoryResult = sendEvent(invEvent);
				AtomicInteger atomicPrice = inventoryResult.get();
				if (atomicPrice == null){
					throw new TimerServiceDoesnNotExistException("Ask for tick resolved as null. TimeService does not exist?");
				}
				if (atomicPrice.intValue() < 0){
					System.out.println("Could not get books price");
					complete(orderEvent, null);
					return;
				}
				//lets charge the customer
				try {
					moneyRegister.chargeCreditCard(orderEvent.getCustomer(), atomicPrice.intValue());
				} catch (Exception e) {
					e.printStackTrace();
					BookSemaphoreHolder.getInstance().release(orderEvent.getOrderedBook());
					complete(orderEvent, null);
					return;
				}
				//lets take book
				Future<Boolean> takeRes = sendEvent(new TakeBookEvent(orderEvent.getOrderedBook()));
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
				AtomicInteger AtomicIssuedTick = issuedTick.get();
				if (isTakeBookSucceeded == null){
					BookSemaphoreHolder.getInstance().release(orderEvent.getOrderedBook());
					throw new TimerServiceDoesnNotExistException("Ask for tick resolved as null. TimeService does not exist?");
				}
				OrderReceipt orderReceipt = new OrderReceipt(orders, getName(), orderEvent.getCustomer().getId(), orderEvent.getOrderedBook(), inventoryResult.get().intValue(), proccessingTick, orderEvent.getOrderTick(), AtomicIssuedTick.intValue());
				complete(orderEvent, orderReceipt);

				DeliveryEvent deliveryEvent = new DeliveryEvent(orderEvent.getCustomer());
				sendEvent(deliveryEvent);
			}
		});
		subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>(){
			@Override
			public void call(TerminationBroadcast c){
				System.out.println("All Microservices are Terminated");
				unregister();
				terminate();
			}
		});
	}
	private class BookNotInInventoryException extends RuntimeException{
		private String doc;
		public BookNotInInventoryException(){this.doc = "";}
		public BookNotInInventoryException(String doc){
			this.doc = doc;
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