package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;


/*ToDO:
	1. Thread safety of the class - partial sync maybe?
	Functions: 	!!sendEvent - figure out how to return the future type
				unregister - probably wrong, need to think about the syncing here

				SubscribeEvent, SubscribeBroadCast - good, unsure about the syncing...
				complete - good, though might need sync. Arent sure about the result types though
				sendBroadCast - i think its ok, check sync
				Register - good?
				awaitMessage - i think it looks good.


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageBusImplHolder {
		private static volatile MessageBusImpl instance = new MessageBusImpl();
	}
	// Added fields:
	// The single instance of the MessageBusImpl class
	// Maps that hold the subscriptions of the services to each message
	private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> eventsSubs;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastsSubs;
	// Map that holds the registered services with their queues of messages
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> registeredServices;
	private ConcurrentHashMap<Event<?>, Future<?>> eventsFutures;


    // Private constructor to prevent instantiation from outside
    private MessageBusImpl() {
        eventsSubs = new ConcurrentHashMap<>();
		broadcastsSubs = new ConcurrentHashMap<>();
		registeredServices = new ConcurrentHashMap<>();
		eventsFutures = new ConcurrentHashMap<>();
		//Inserts into each map the Events and broadcasts possible
		eventsSubs.put(PoseEvent.class, new ConcurrentLinkedQueue<MicroService>());
		eventsSubs.put(DetectObjectsEvent.class, new ConcurrentLinkedQueue<MicroService>());
		eventsSubs.put(TrackedObjectsEvent.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(TickBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(TerminatedBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(CrashedBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
	}

    // Static method to return the instance of MessageBusImpl (singleton)
    public static MessageBusImpl getInstance() {
        return MessageBusImplHolder.instance;
    }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> q = eventsSubs.get(type);
		//Locking the queue of the event we want to add to, so only m can register
		synchronized(q){
			q.offer(m);
			//eventsSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> q = broadcastsSubs.get(type);
		//Locking the queue of the event we want to add to, so only m can register
		synchronized(q){
			q.offer(m);
			//q.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		}
	} 

	//I used the type of each future to be what i assumed was the fitting one, but i could be wrong.
	//Need to check if cloudPoint, pose, and landmark are the right classes.
	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized(eventsFutures){
			// Unsafe casting, maybe there's a better idea
			Future<T> f = (Future<T>)eventsFutures.get(e);
			f.resolve(result);
			f.notifyAll();
			eventsFutures.remove(e);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// Gets the queue of microServices that are subscripted to this broadcast type
		ConcurrentLinkedQueue<MicroService> q = broadcastsSubs.get(b.getClass());

		// If a microService in the queue is not registered, deletes it
		// Otherwise, adds the message to the microService's queue
		synchronized(q){
			for(MicroService m : q){
				//Do we need this? Maybe make sure to take them out of subs lists if unregistered
				/* if(!(registeredServices.containsKey(m))){
					q.remove(m);
				}*/
				ConcurrentLinkedQueue<Message> lst = registeredServices.get(m);
				synchronized(lst){
					lst.offer(b);
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<T>();
		eventsFutures.computeIfAbsent(e, k -> f);	
		ConcurrentLinkedQueue<MicroService> q = eventsSubs.get(e.getClass()); // Gets the queue of microServices that are subscripted to this event type
		synchronized(q){
			//Takes the first of the queue and adds him to the back - thats the chosen Thread
			MicroService m = q.poll();
			if(m==null){
				return null;
			}
			else{
				q.add(m);
				ConcurrentLinkedQueue<Message> lst = registeredServices.get(m);
				synchronized(lst){
					lst.offer(e);
				}
				lst.notifyAll();
			}
			
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		// Adds the microService to the map with an empty queue that will contain m's messages
		registeredServices.computeIfAbsent(m, k -> new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue<MicroService> q1 = eventsSubs.get(PoseEvent.class);
		synchronized(q1){
			q1.remove(m);
		}
		ConcurrentLinkedQueue<MicroService> q2 = eventsSubs.get(DetectObjectsEvent.class);
		synchronized(q2){
			q2.remove(m);
		}
		ConcurrentLinkedQueue<MicroService> q3 = eventsSubs.get(TrackedObjectsEvent.class);
		synchronized(q3){
			q3.remove(m);
		}
		ConcurrentLinkedQueue<MicroService> q4 = broadcastsSubs.get(TickBroadcast.class);
		synchronized(q4){
			q4.remove(m);
		}
		ConcurrentLinkedQueue<MicroService> q5 = eventsSubs.get(CrashedBroadcast.class);
		synchronized(q5){
			q5.remove(m);
		}
		ConcurrentLinkedQueue<MicroService> q6 = eventsSubs.get(TerminatedBroadcast.class);
		synchronized(q6){
			q6.remove(m);
		}
		registeredServices.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> q = registeredServices.get(m);
		Message message=null;
		synchronized(q){
			while(q.isEmpty()){
				try{
					q.wait();
					message = q.remove();
					q.notifyAll();
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
		}
		return message;
	}
}
