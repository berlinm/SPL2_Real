package bgu.spl.mics;

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
		return SingletonHolder.instance;
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
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(BroadcastSubscribe.containsKey(type)) {
			BroadcastSubscribe.get(type).add(m);
		}else {
			BroadcastSubscribe.put(type,new LinkedBlockingDeque<MicroService>());
			BroadcastSubscribe.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		this.EventFut.get(e).resolve(result);
		this.EventFut.remove(e); //not sure
	}
	@Override
	public  void sendBroadcast(Broadcast b) {
		if(BroadcastSubscribe.containsKey(b.getClass())) {
			for (MicroService m : BroadcastSubscribe.get(b.getClass())) {
				try {
					srvQueue.get(m).add(b);
					synchronized (m) {
						m.notify();
					}
				} catch (NullPointerException exception) { /*Other thread deleted m*/ }
			}
		}
	}
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		Future<T> res=new Future<T>();
		MicroService m = this.EventSubscribe.get(e.getClass()).remove();
		this.EventSubscribe.get(e.getClass()).add(m);
		srvQueue.get(m).add(e);
		this.EventFut.put(e, res);
		synchronized (m) {
			m.notify();
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
        for (Class c:this.BroadcastSubscribe.keySet()){
            for (MicroService microService:this.BroadcastSubscribe.get(c)){
                if (microService.equals(m)){
                    this.BroadcastSubscribe.get(c).remove(m);
                }
            }
        }
        //lets remove all event subscriptions
        for (Class c:this.EventSubscribe.keySet()){
            for (MicroService microService:this.EventSubscribe.get(c)){
                if (microService.equals(m)){
                    this.EventSubscribe.get(c).remove(m);
                }
            }
        }
		srvQueue.remove(m);// maybe works :()
		//					you are an idiot
	}
	//I think that because each MicroService is calling this method with (this) as argument, no concurrency problems here,
	//but needs to be checked again
	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {
		if(!this.srvQueue.containsKey(m)){
			this.srvQueue.put(m,new LinkedBlockingQueue<Message>());
		}
			while (this.srvQueue.get(m).isEmpty()) {
				try {
					synchronized (m) {
						m.wait();
					}
				}catch (Exception e){e.printStackTrace();}
			}
			return this.srvQueue.get(m).remove();
	}

	

}
