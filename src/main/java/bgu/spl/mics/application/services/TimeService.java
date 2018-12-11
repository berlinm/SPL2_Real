package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private Timer m_Timer;
	private int currentTick, duration, speed;
	private TimeService(int speed, int duration) {
		super("Global Timer");
		this.currentTick = 0;
		this.duration = duration;
		this.speed = speed;
	}
	//TODO: comment inside
	@Override
	protected void initialize() {
		this.m_Timer = new Timer();
		this.m_Timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (currentTick < duration)
				{
					MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(currentTick));
					currentTick++;
				}
				else {
					//means the timer is up longer than the set duration (need to be terminated?)
				}
			}
		}, 0, this.speed);
	}
	public int getCurrentTick(){
		return this.currentTick;
	}
}
