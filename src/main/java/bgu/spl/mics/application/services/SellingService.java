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
		subscribeEvent(BookOrderEvent.class, ev -> {
			CheckInventoryEvent it = new CheckInventoryEvent(ev.getOrderedBook());
			Future<AtomicInteger> result = sendEvent(it);
			if (result.get() != null) {
				TakeBookEvent takeBookEvent=new TakeBookEvent(ev.getOrderedBook());
				sendEvent(takeBookEvent);
				try {
					this.moneyRegister.chargeCreditCard(ev.getCustomer(), result.get().intValue());
				} catch (Exception e) {
					//not enough money
					e.printStackTrace();
				}
				//i have no idea right now what is the diffrence between each tick in the order recipet constructor
				OrderReceipt orderReceipt=new OrderReceipt(orders,this.getName(),ev.getCustomer().getId(),ev.getOrderedBook(),result.get().intValue(),ev.getCurrTick(),ev.getCurrTick(),ev.getCurrTick());
				complete(ev,orderReceipt);
				terminate();

			} else {
				System.out.println("This book don't exist or out of stock in our Inventory");
				terminate();
			}

		});

	}
}