package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Messages.CheckInventoryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
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

		subscribeEvent(CheckInventoryEvent.class,checkInventoryEvent->{
			int price = inventory.checkAvailabiltyAndGetPrice(checkInventoryEvent.getName());
			complete(checkInventoryEvent,new AtomicInteger(price));
		});
		subscribeEvent(TakeBookEvent.class,takeBookEvent ->{
			OrderResult result = inventory.take(takeBookEvent.getName());
			if (result == OrderResult.NOT_IN_STOCK) {
				complete(takeBookEvent, false);
			}
			else complete(takeBookEvent, true);
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
