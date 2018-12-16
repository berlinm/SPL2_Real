package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Messages.CheckInventoryEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	Inventory inventory;
	public InventoryService(String name) {
		super(name);
		inventory=Inventory.getInstance();
	}
	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		subscribeEvent(CheckInventoryEvent.class,checkInventoryEvent->{
			System.out.println(getName() + " got new CheckInventoryEvent from " + checkInventoryEvent.getSender());
			int price = inventory.checkAvailabiltyAndGetPrice(checkInventoryEvent.getBookName());
			complete(checkInventoryEvent,new AtomicInteger(price));
		});
		subscribeEvent(TakeBookEvent.class, new Callback<TakeBookEvent>() {
					@Override
					public void call(TakeBookEvent takeBookEvent) {
						System.out.println(getName()+" got TakeBookEvent from " + takeBookEvent.getSenderName() + " on book" + takeBookEvent.getBookName());
						OrderResult result = inventory.take(takeBookEvent.getBookName());
						if (result == OrderResult.NOT_IN_STOCK) {
							complete(takeBookEvent, false);
						}
						else complete(takeBookEvent, true);
						System.out.println(getName() + " finished executing TakeBookEvent from " + takeBookEvent.getSenderName() + " on book " + takeBookEvent.getBookName());
					}
				});
				subscribeBroadcast(TerminationBroadcast.class, new Callback<TerminationBroadcast>() {
					@Override
					public void call(TerminationBroadcast c) {
						System.out.println(getName() + " got Termination Broadcast");
						unregister();
						terminate();
						System.out.println(getName() + " Terminated");
					}
				});
		System.out.println(this.getName() + " Initialization ended");

	}

}
