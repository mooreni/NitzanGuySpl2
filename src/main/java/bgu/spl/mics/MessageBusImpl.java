package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/*ToDO:
	1. Figure out the Events - some Event handler is needed in order to make an Event object that
	   holds a Future object
	2. Thread safety of the class - partial sync maybe?


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	// Added fields:
	// The single instance of the MessageBusImpl class
    private static final MessageBusImpl instance = new MessageBusImpl();
	// Maps that hold the subscriptions of the services to each message
	private ConcurrentHashMap<Class<? extends Event<?>>, Queue<MicroService>> eventsSubs;
	private ConcurrentHashMap<Class<? extends Broadcast>, Queue<MicroService>> broadcastsSubs;
	// Map that holds the registered services with their queues of messages
	private ConcurrentHashMap<MicroService, Queue<Message>> registeredServices;

    // Private constructor to prevent instantiation from outside
    private MessageBusImpl() {
        eventsSubs = new ConcurrentHashMap<>();
		broadcastsSubs = new ConcurrentHashMap<>();
		registeredServices = new ConcurrentHashMap<>();
    }

    // Static method to return the instance of MessageBusImpl (singleton)
    public static MessageBusImpl getInstance() {
        return instance;
    }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventsSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastsSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {

		// Gets the queue of microServices that are subscripted to this broadcast type
		Queue<MicroService> q = broadcastsSubs.get(b.getClass());

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
		Queue<MicroService> q = eventsSubs.get(e.getClass());

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

		// Need to figure out how the change in the event.future is made and return it
		return e.Future;
	}

	@Override
	public void register(MicroService m) {
		// Adds the microService to the map with an empty queue that will contain m's messages
		registeredServices.computeIfAbsent(m, k -> new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		registeredServices.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
