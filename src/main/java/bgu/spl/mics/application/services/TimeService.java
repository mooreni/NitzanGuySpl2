package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

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
        //For every tick received, we will sleep for the tickTime and send another tick
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            try{
                Thread.sleep(tickTime*1000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                terminate();
            }
            currentTick++;
            StatisticalFolder.getInstance().increaseSystemRuntime();
            if(currentTick == duration){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
            sendBroadcast(new TickBroadcast(currentTick));

        });
        //Send first tick
        sendBroadcast(new TickBroadcast(currentTick));

        //Terminate early if fusionslam terminated
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            if(terminateMessage.getSenderName().equals("FusionSlam")){
                terminate();
            }
        });
        //If one of the services crashed, terminate too
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage -> terminate());
    }
}
