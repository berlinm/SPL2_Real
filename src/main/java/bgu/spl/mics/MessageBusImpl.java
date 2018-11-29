package bgu.spl.mics;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Class<? extends Event<?>>,BlockingQueue<MicroService>> EvenetSubscribe = new ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>>();
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
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) { //not sure about Synchronized
	    EvenetSubscribe.get(type).add(m);
	}

	@Override
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		BroadcastSubscribe.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		this.EventFut.get(e).resolve(result);
		this.EventFut.remove(e); //not sure
	}
	//TODO: think how to do thrad safe
	@Override
	public void sendBroadcast(Broadcast b) {
        for(MicroService m : BroadcastSubscribe.get(b)){
        	srvQueue.get(m).add(b);
        }
	}

	
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) { //again not sure about synchronized
		Future<T> res=new Future<T>();
		MicroService m = this.EvenetSubscribe.get(e).remove();
		this.EvenetSubscribe.get(e).add(m);
		srvQueue.get(m).add(e);
		this.EventFut.put(e, res);
		return res;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> myqueue=new LinkedBlockingQueue<Message>();
		srvQueue.put(m,myqueue);
	}

	@Override
	public synchronized void unregister(MicroService m) {
		srvQueue.remove(m);// maybe works :()

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
