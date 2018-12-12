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
				//lets check the current tick for the proccessing tick
				Future<AtomicInteger> currentTick = sendEvent(new AskForTickEvent());
				int proccessingTick = currentTick.get().intValue();
				//lets get price
				CheckInventoryEvent invEvent = new CheckInventoryEvent(orderEvent.getOrderedBook());
				Future<AtomicInteger> inventoryResult = sendEvent(invEvent);
				int price = inventoryResult.get().intValue();
				if (price < 0){
					System.out.println("Could not get books price");
					return;
				}
				//lets charge the customer
				try {
					moneyRegister.chargeCreditCard(orderEvent.getCustomer(), price);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				//lets take book
				Future<Boolean> sendRes = sendEvent(new TakeBookEvent(orderEvent.getOrderedBook()));
				if (!sendRes.get()){
					throw new BookNotInInventoryException("Something wrong. selling service " + getName() + "tried to order "
					+ orderEvent.getOrderedBook() + "And the price was not -1 even though book was not in stock or not exists.");
				}
				//lets get the current tick for the issued tick
				Future<AtomicInteger> issuedTick = sendEvent(new AskForTickEvent());
				OrderReceipt orderReceipt = new OrderReceipt(orders, getName(), orderEvent.getCustomer().getId(), orderEvent.getOrderedBook(), inventoryResult.get().intValue(), proccessingTick, orderEvent.getOrderTick(), issuedTick.get().intValue());
				complete(orderEvent, orderReceipt);
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
}