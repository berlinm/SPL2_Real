package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import bgu.spl.mics.MySemaphore;

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
	private static ResourcesHolder Instance = new ResourcesHolder();
	private BlockingQueue<DeliveryVehicle> freeVehicles;
	private BlockingDeque<Future<DeliveryVehicle>> waitingFutures;
	private Semaphore _sem;
	private MySemaphore mySemaphore;
	private ResourcesHolder(){
		freeVehicles = new LinkedBlockingQueue<DeliveryVehicle>();
		waitingFutures = new LinkedBlockingDeque<Future<DeliveryVehicle>>();
		this.mySemaphore = new MySemaphore();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return Instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		mySemaphore.acquireAn_Acquire();
		if (this._sem == null)
			throw new NotInitializedSemaphore();
		Future<DeliveryVehicle> future=new Future<DeliveryVehicle>();
		if(_sem.tryAcquire()){
			future.resolve(freeVehicles.remove());
		}
		else {
			waitingFutures.addLast(future);
		}
		mySemaphore.releaseAn_Acquire();
		return future;
	}
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		this.mySemaphore.acquire_A_Release();
		if (this._sem == null)
			throw new NotInitializedSemaphore();
		if(waitingFutures.size()!=0) {
			Future<DeliveryVehicle> future = waitingFutures.remove();
			future.resolve(vehicle);
		} else {
			freeVehicles.add(vehicle);
			_sem.release();
		}
		this.mySemaphore.releaseA_Release();
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		_sem = new Semaphore(vehicles.length);
		for(int i=0;i<vehicles.length;i++){
			freeVehicles.add(vehicles[i]);
		}
	}
	private class NotInitializedSemaphore extends RuntimeException{}
}
