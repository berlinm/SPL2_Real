package bgu.spl.mics;

import bgu.spl.mics.Messages.AskForTickEvent;
import bgu.spl.mics.Messages.TerminationBroadcast;
import bgu.spl.mics.application.services.TimeService;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Class<? extends Event<?>>,BlockingQueue<MicroService>> EventSubscribe = new ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>>();
	private ConcurrentHashMap<Class<? extends Broadcast>,BlockingDeque<MicroService>> BroadcastSubscribe = new ConcurrentHashMap<Class<? extends Broadcast>,BlockingDeque<MicroService>>();
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> srvQueue = new ConcurrentHashMap<MicroService,BlockingQueue<Message>>();
	private ConcurrentHashMap<Event<?>, Future> EventFut = new ConcurrentHashMap<Event<?>, Future>();

	//implement of the singleton design pattern
	private static class SingletonHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	public static MessageBusImpl getInstance(){
		MessageBusImpl instance = SingletonHolder.instance;
		return instance;
	}

	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(EventSubscribe.containsKey(type)){
	    EventSubscribe.get(type).add(m);
		}else{
			EventSubscribe.put(type,new LinkedBlockingQueue<MicroService>());
			EventSubscribe.get(type).add(m);
		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(BroadcastSubscribe.containsKey(type)) {
			BroadcastSubscribe.get(type).add(m);
			System.out.println("service: " + m.getName() + " subscribed broadcast" + type);
		}else {
			BroadcastSubscribe.put(type,new LinkedBlockingDeque<MicroService>());
			BroadcastSubscribe.get(type).add(m);
			System.out.println("service: " + m.getName() + " subscribed broadcast" + type);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		this.EventFut.get(e).resolve(result);
		this.EventFut.remove(e);
	}
	@Override
	public void sendBroadcast(Broadcast b) {
		if(BroadcastSubscribe.containsKey(b.getClass())) {
			for (MicroService m : BroadcastSubscribe.get(b.getClass())) {
				try {
					srvQueue.get(m).add(b);
					System.out.println("Micro service: " + m.getName() + " Notified by broadcast: " + b.getClass());
					synchronized (this.srvQueue.get(m)){
						this.srvQueue.get(m).notifyAll();
					}
				} catch (NullPointerException exception) {}
			}
		}
	}
	@Override
	public <T> Future<T> sendEvent(Event<T> e) { //changes some things to fix sone problem orel had need to be checked
		Future<T> res=new Future<T>();
		if(this.EventSubscribe.get(e.getClass()).size() > 1) {
			boolean isFoundMicroService = false;
			for (int i = 0; i < EventSubscribe.get(e.getClass()).size(); i++){
				MicroService m = this.EventSubscribe.get(e.getClass()).remove();
				this.EventSubscribe.get(e.getClass()).add(m);
				if (!m.isTerminated()){
					srvQueue.get(m).add(e);
					this.EventFut.put(e, res);
					synchronized (this.srvQueue.get(m)) {
						this.srvQueue.get(m).notifyAll();
					}
					isFoundMicroService = true;
					break;
				}
			}
			if (!isFoundMicroService)
				res.resolve(null);
		} else if(this.EventSubscribe.get(e.getClass()).size()==0){
			//then no microservice to send the event to
			res.resolve(null);
		} else {
			MicroService m=this.EventSubscribe.get(e.getClass()).peek();
			if (!m.isTerminated()){
				srvQueue.get(m).add(e);
				this.EventFut.put(e, res);
				synchronized (this.srvQueue.get(m)) {
					this.srvQueue.get(m).notifyAll();
				}
			}
			else {
				res.resolve(null);
			}
		}
		return res;
	}
	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> myqueue=new LinkedBlockingQueue<Message>();
		srvQueue.put(m,myqueue);
	}
	@Override
	public synchronized void unregister(MicroService m) {
		//lets remove all broadcasts subscriptions
		System.out.println("Micro service: " + m.getName() + "unregistered from bus");
        for (Class c:this.BroadcastSubscribe.keySet()){
            for (MicroService microService:this.BroadcastSubscribe.get(c)){
                if (microService == m){
                    this.BroadcastSubscribe.get(c).remove(m);
                }
            }
        }
        //lets remove all event subscriptions
        for (Class c:this.EventSubscribe.keySet()){
            for (MicroService microService:this.EventSubscribe.get(c)){
                if (microService == m){
                    this.EventSubscribe.get(c).remove(m);
                }
            }
        }
		srvQueue.remove(m);
	}
	//I think that because each MicroService is calling this method with (this) as argument, no concurrency problems here,
	//but needs to be checked again
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		System.out.println("service: " + m.getName() + " entered bus.awaitMessage");
		if(!this.srvQueue.containsKey(m)){
			this.srvQueue.put(m ,new LinkedBlockingQueue<Message>());
		}
		while (this.srvQueue.get(m).isEmpty()) {
			try {
				synchronized(this.srvQueue.get(m)) {
					System.out.println("service: " + m.getName() + " goes waits for notify");
					this.srvQueue.get(m).wait();
					System.out.println("service: " + m.getName() + " exited wait (was notified)");
				}
			}catch (Exception e){e.printStackTrace();}
		}
		for (Message message: srvQueue.get(m)) {
			if (message instanceof TerminationBroadcast){
				System.out.println("Micro service " + m.getName() + " got a termination broadcast (from bus)" );
				srvQueue.get(m).remove(message);
				return message;
			}
		}
		return this.srvQueue.get(m).remove();
	}
}