package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.DeliveryEvent;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

import java.util.concurrent.BlockingQueue;

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

	BlockingQueue<DeliveryVehicle> deliveryVehicles;
	/**
     * Retrieves the single instance of this class.
     */

	private  static class SingletonHolder{
		private static ResourcesHolder instance=new ResourcesHolder();
	}

	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		//not sure
		return MessageBusImpl.getInstance().sendEvent(new DeliveryEvent());
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		//TODO: Implement this
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		//TODO: Implement this
	}

}
