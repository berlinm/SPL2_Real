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

	private ConcurrentHashMap<Class<?>,BlockingQueue<MicroService>> EvenetSubscribe=new ConcurrentHashMap<Class<?>, BlockingQueue<MicroService>>();
	private ConcurrentHashMap<MicroService,BlockingDeque<Class<?>>> BroadcastSubscribe=new ConcurrentHashMap<MicroService,BlockingDeque<Class<?>>>();
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> FetchMap =new ConcurrentHashMap<MicroService,BlockingQueue<Message>>();

	//implement of the singleton design pattern
	private static class SingletonHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	public static MessageBusImpl getInstance(){

		return SingletonHolder.instance;
	}

	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {//not sure about Synchronized
	    BlockingQueue<MicroService> curr=EvenetSubscribe.get(type);
	    curr.add(m);


	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		BlockingDeque<Class<?>> curr=BroadcastSubscribe.get(m);
		curr.add(type);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
	    Boolean isfound;
        for(MicroService m : BroadcastSubscribe.keySet()){
            isfound=false;
            BlockingDeque<Class<?>> T=BroadcastSubscribe.get(m);
            if(T.contains(b)){
                isfound=true;
            }

            if(isfound){
                FetchMap.get(m).add(b);
            }
        }
	}

	
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) { //again not sure about synchronized
		Future<T> ANS=new Future<T>();

		BlockingQueue<MicroService> mic=this.EvenetSubscribe.get(e);
		MicroService m=mic.remove();
		mic.add(m);

		FetchMap.get(m).add(e);

		//idk how they expect me to return future

		return ANS;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> myqueue=new LinkedBlockingQueue<Message>();
		FetchMap.put(m,myqueue);

	}

	@Override
	public void unregister(MicroService m) {

		FetchMap.remove(m);// maybe works :()

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
