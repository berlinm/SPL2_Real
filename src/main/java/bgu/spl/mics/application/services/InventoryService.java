package bgu.spl.mics.application.services;

import bgu.spl.mics.CheckInventoryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TakeBookEvent;
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

		subscribeEvent(CheckInventoryEvent.class,ev->{

			int x=inventory.checkAvailabiltyAndGetPrice(ev.getName());
			if(x==-1){
				complete(ev,null);
			}else {
				Future<AtomicInteger> future=new Future<AtomicInteger>();
				AtomicInteger atomicInteger=new AtomicInteger(x);
				future.resolve(atomicInteger);
				complete(ev,future.get());
			}

			terminate();
		});


		subscribeEvent(TakeBookEvent.class,ev->{
			OrderResult result=inventory.take(ev.getName());
			terminate();
		});
		
	}

}
