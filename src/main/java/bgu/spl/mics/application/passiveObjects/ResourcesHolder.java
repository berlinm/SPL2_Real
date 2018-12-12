package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.DeliveryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

import java.util.concurrent.*;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private ResourcesHolder Instance=new ResourcesHolder();

	private BlockingQueue<DeliveryVehicle> deliveryVehicles;
	private BlockingDeque<Future<DeliveryVehicle>> unresolvedFuture;
	private Semaphore _sem;
	/**
     * Retrieves the single instance of this class.
     */

	private ResourcesHolder(){
		_sem=new Semaphore(0);
		deliveryVehicles=new LinkedBlockingQueue<DeliveryVehicle>();
		unresolvedFuture=new LinkedBlockingDeque<Future<DeliveryVehicle>>();
	}

	public ResourcesHolder getInstance() {
		return this.Instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future=new Future<DeliveryVehicle>();

		if(_sem.tryAcquire()){

			future.resolve(deliveryVehicles.remove());

		}else {
			unresolvedFuture.addLast(future);

		}

		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {

		if(unresolvedFuture.size()!=0) {
			Future<DeliveryVehicle> future = unresolvedFuture.remove();
			future.resolve(vehicle);
		} else {

			deliveryVehicles.add(vehicle);
			_sem.release();
		}


	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {

		for(int i=0;i<vehicles.length;i++){

			deliveryVehicles.add(vehicles[i]);
		}

	}

}
