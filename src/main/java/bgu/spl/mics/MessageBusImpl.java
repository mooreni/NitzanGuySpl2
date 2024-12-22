package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;


/*ToDO:
	1. Figure out the Events - some Event handler is needed in order to make an Event object that
	   holds a Future object
	2. Thread safety of the class - partial sync maybe?
	3.


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	//Class should have a list/hashMap? of Event Types, each holding a list of microServices subscribed

	// Added fields:
	// The single instance of the MessageBusImpl class
    private static final MessageBusImpl instance = new MessageBusImpl();
	// Maps that hold the subscriptions of the services to each message
	private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> eventsSubs;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastsSubs;
	// Map that holds the registered services with their queues of messages
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> registeredServices;

	Object lockEventSubs, lockBroadcastSubs, lockRegisteredServices;

    // Private constructor to prevent instantiation from outside
    private MessageBusImpl() {
        eventsSubs = new ConcurrentHashMap<>();
		broadcastsSubs = new ConcurrentHashMap<>();
		registeredServices = new ConcurrentHashMap<>();
		eventsSubs.put(PoseEvent.class, new ConcurrentLinkedQueue<MicroService>());
		eventsSubs.put(DetectObjectsEvent.class, new ConcurrentLinkedQueue<MicroService>());
		eventsSubs.put(TrackedObjectsEvent.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(TickBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(TerminatedBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
		broadcastsSubs.put(CrashedBroadcast.class, new ConcurrentLinkedQueue<MicroService>());
		lockEventSubs=new Object();
		lockBroadcastSubs=new Object();
		lockRegisteredServices=new Object();
    }

    // Static method to return the instance of MessageBusImpl (singleton)
    public static MessageBusImpl getInstance() {
        return instance;
    }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized(lockEventSubs){
			eventsSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized(lockBroadcastSubs){
			broadcastsSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		}
	}


	//I used the type of each future to be what i assumed was the fitting one, but i could be wrong.
	//Need to check if cloudPoint, pose, and landmark are the right classes.
	@Override
	public <T> void complete(Event<T> e, T result) {
		if(e instanceof DetectObjectsEvent && result instanceof CloudPoint){
			((DetectObjectsEvent)e).getFuture().resolve((CloudPoint)result);
		}
		else if(e instanceof PoseEvent && result instanceof Pose){
			((PoseEvent)e).getFuture().resolve((Pose)result);
		}
		else if(e instanceof TrackedObjectsEvent && result instanceof LandMark){
			((TrackedObjectsEvent)e).getFuture().resolve((LandMark)result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		// Gets the queue of microServices that are subscripted to this broadcast type
		ConcurrentLinkedQueue<MicroService> q = broadcastsSubs.get(b.getClass());

		// If a microService in the queue is not registered, deletes it
		// Otherwise, adds the message to the microService's queue
		for(MicroService m : q){
			if(!(registeredServices.containsKey(m))){
				q.remove(m);
			}
			else{
				registeredServices.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// Gets the queue of microServices that are subscripted to this event type
		ConcurrentLinkedQueue<MicroService> q = eventsSubs.get(e.getClass());
		// If a microService in the queue is not registered, deletes it
		// Otherwise, adds the message to the microService's queue
		for(MicroService m : q){
			if(!(registeredServices.containsKey(m))){
				q.remove(m);
			}
			else{
				registeredServices.get(m).add(e);
				//return 
			}
		}

		// Need to figure out how the change in the event.future is made and return it
		return null;
	}

	@Override
	public void register(MicroService m) {
		// Adds the microService to the map with an empty queue that will contain m's messages
		registeredServices.computeIfAbsent(m, k -> new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue q1 = eventsSubs.get(PoseEvent.class);
		synchronized(q1){
			q1.remove(m);
		}
		ConcurrentLinkedQueue q2 = eventsSubs.get(DetectObjectsEvent.class);
		synchronized(q2){
			q2.remove(m);
		}
		ConcurrentLinkedQueue q3 = eventsSubs.get(TrackedObjectsEvent.class);
		synchronized(q3){
			q3.remove(m);
		}
		ConcurrentLinkedQueue q4 = broadcastsSubs.get(TickBroadcast.class);
		synchronized(q4){
			q4.remove(m);
		}
		ConcurrentLinkedQueue q5 = eventsSubs.get(CrashedBroadcast.class);
		synchronized(q5){
			q5.remove(m);
		}
		ConcurrentLinkedQueue q6 = eventsSubs.get(TerminatedBroadcast.class);
		synchronized(q6){
			q6.remove(m);
		}
		registeredServices.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> q = registeredServices.get(m);
		Message mess=null;
		synchronized(q){
			try{
				while(q.isEmpty()){
					q.wait();
				}
				mess = q.remove();
				q.notifyAll();
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
		return mess;
	}

	

}
