package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.Messages.TakeBookEvent;
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
				CheckInventoryEvent invEvent = new CheckInventoryEvent(orderEvent.getOrderedBook());
				Future<AtomicInteger> inventoryResult = sendEvent(invEvent);
				int price = inventoryResult.get().intValue();
				if (price >= 0) {
					TakeBookEvent takeBookEvent = new TakeBookEvent(orderEvent.getOrderedBook());
					sendEvent(takeBookEvent);
					try {
						moneyRegister.chargeCreditCard(orderEvent.getCustomer(), price);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					//according orel there is a need of sending an event to the timer asking for the current tick. this tick will be the issuedTick
					OrderReceipt orderReceipt = new OrderReceipt(orders, getName(), orderEvent.getCustomer().getId(), orderEvent.getOrderedBook(), inventoryResult.get().intValue(), orderEvent.getOrderTick(), orderEvent.getOrderTick(), orderEvent.getOrderTick());
					complete(orderEvent, orderReceipt);
					terminate();
				} else {
					System.out.println("This book don't exist or out of stock in our Inventory");
					terminate();
				}
			}
		});

	}
}