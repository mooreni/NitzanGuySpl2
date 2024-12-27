package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;

import java.util.List;
import java.util.ArrayList;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarWorkerTracker;
    private List<DetectObjectsEvent> oldEvents; //Saves up old DetectObject messages it got

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService" + LiDarWorkerTracker.getID());
        this.liDarWorkerTracker = LiDarWorkerTracker;
        oldEvents = new ArrayList<>();
        // TODO Implement this - do we need to add something else?
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if(liDarWorkerTracker.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                int currentTick = tickMessage.getTickTime();
                for(DetectObjectsEvent obj : oldEvents){
                    //Looks for the event from the relevant time - check if its + or - here
                    if(obj.getStampedDetectedObjects().getTime() == currentTick-liDarWorkerTracker.getFrequency()){
                        /*
                        We need to do several things here.
                        1. For every event, we need to go over obj.getDetectedObjects() and do ____ with it and the dataBase
                            a. the database should be read from a lidar_data.json file thats given to us, like in the examples folder
                        2. with the result of this operation, we need to:
                            a. send a TrackedObjectEvent to fusionSLAM
                                -SendEvent gets back a future - do we need to do something with it? 
                            b. save the results into the dataBase
                            c. save the results into lastTrackedObjects in LiDarWorkerTracker - a field of the last objects that were tracked
                        3. Complete the event we just solved - the DetectObjectsEvent. We need to do a complete to it
                        */
                    }
                }
            }        
        });
        subscribeEvent(DetectObjectsEvent.class, detectObjectMessage ->{
            if(liDarWorkerTracker.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                oldEvents.add(detectObjectMessage);
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            //If the service that terminates was the time service, terminate too
            if(terminateMessage.getSenderName().compareTo("TimeService") ==0){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage -> terminate());    
    }
}
