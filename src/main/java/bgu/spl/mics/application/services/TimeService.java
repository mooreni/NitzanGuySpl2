package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int tickTime;
    private int duration;
    private int currentTick;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        tickTime = TickTime;
        duration = Duration;
        currentTick=0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        //While we didnt reach the number of ticks needed, we send broadcasts 
        while(currentTick <= duration){
            try{
                sendBroadcast(new TickBroadcast(currentTick));
                currentTick = currentTick++;
                Thread.sleep(tickTime);
                //Will go out into the run function in MicroService, where we broadCast termination
                 
            }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            }
        }
        terminate();
        sendBroadcast(new TerminatedBroadcast(getName()));
    }
}
