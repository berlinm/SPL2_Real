package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Messages.AskForTickEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.Messages.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
	public TimeService(int speed, int duration) {
		super("Global Timer");
		this.currentTick = 0;
		this.duration = duration;
		this.speed = speed;
	}
	//TODO: comment inside
	@Override
	protected void initialize() {
		System.out.println(this.getName() + " Initialization started");
		this.m_Timer = new Timer();
		this.m_Timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (currentTick < duration)
				{
					sendBroadcast(new TickBroadcast(currentTick));
					currentTick++;
				}
				else {
					sendBroadcast(new TerminationBroadcast());
					unregister();
					terminate();
				}
			}
		}, 0, this.speed);
		subscribeEvent(AskForTickEvent.class, new Callback<AskForTickEvent>() {
			@Override
			public void call(AskForTickEvent c) {
				System.out.println("Timer received AskForTickEvent, Current Tick" + currentTick);
				complete(c, new AtomicInteger(currentTick));
			}
		});
		System.out.println(this.getName() + " Initialization ended");
	}
}
