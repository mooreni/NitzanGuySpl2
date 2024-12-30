package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

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
    private int nextToProcess = 0; //The next event to process
    private LiDarDataBase liDarDataBase = LiDarDataBase.getInstance(); 
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
                List<TrackedObject> trackedObjects = trackObjects(currentTick);
                /*
                We need to do several things here.
                1. For every event, we need to go over obj.getDetectedObjects() and do ____ with it and the dataBase
                    a. the database should be read from a lidar_data.json file thats given to us, like in the examples folder
                2. with the result of this operation, we need to:
                    a. send a TrackedObjectEvent to fusionSLAM
                        -SendEvent gets back a future - do we need to do something with it?
                    b. save the results into lastTrackedObjects in LiDarWorkerTracker - a field of the last objects that were tracked
                ///////// DONE UNTIL HERE ///////// 
                    c. save the results into the dataBase - ?
                3. Complete the event we just solved - the DetectObjectsEvent. We need to do a complete to it
                */
                sendEvent(new TrackedObjectsEvent(getName(), trackedObjects, currentTick));
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

    private List<TrackedObject> trackObjects(int currentTick){
        List<TrackedObject> trackedObjects = new ArrayList<>();
        //For every event the camera sent, we check if it needs to be processed
        for(int i = nextToProcess; i<oldEvents.size();i++){
            DetectObjectsEvent objEvent = oldEvents.get(i);
            StampedDetectedObjects stampedDetectedObjects = objEvent.getStampedDetectedObjects();
            //Looks for the event from the relevant time - check if its + or - here: page 15
            if(stampedDetectedObjects.getDetectionTime()+liDarWorkerTracker.getFrequency() <= currentTick){
                for(DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()){
                    TrackedObject trackedObject = new TrackedObject(detectedObject.getID(), currentTick, detectedObject.getDescription(),
                                                    liDarDataBase.searchCoordinates(detectedObject, currentTick));
                    trackedObjects.add(trackedObject);
                }
                //Completes the event the camera sent
                complete(objEvent, trackedObjects);
                liDarWorkerTracker.setLastTrackedObjects(trackedObjects);
            }
            nextToProcess++;
        }
        return trackedObjects;
    }
}
