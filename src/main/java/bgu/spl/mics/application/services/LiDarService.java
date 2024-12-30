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
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private int currentTick;
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
        currentTick = 0;
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
                currentTick = tickMessage.getTickTime();
                List<TrackedObject> trackedObjects = trackObjects();
                if(trackedObjects.size() > 0){
                    liDarWorkerTracker.setLastTrackedObjects(trackedObjects);
                    sendEvent(new TrackedObjectsEvent(getName(), trackedObjects, currentTick));
                    StatisticalFolder.getInstance().increaseNumTrackedObjects(trackedObjects.size());
                }
                /*
                We need to do several things here.
                1. For every event, we need to go over obj.getDetectedObjects() and do ____ with it and the dataBase
                    a. the database should be read from a lidar_data.json file thats given to us, like in the examples folder
                2. with the result of this operation, we need to:
                    a. send a TrackedObjectEvent to fusionSLAM
                        -SendEvent gets back a future - do we need to do something with it?
                    b. save the results into lastTrackedObjects in LiDarWorkerTracker - a field of the last objects that were tracked
                3. Complete the event we just solved - the WDetectObjectsEvent. We need to do a complete to it
                ///////// DONE UNTIL HERE ///////// 
                c. save the results into the dataBase - ?
                */
            }        
        });
        subscribeEvent(DetectObjectsEvent.class, detectObjectMessage ->{
            if(liDarWorkerTracker.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                oldEvents.add(detectObjectMessage);
                //Checks if the new event already got processed and send it further
                List<TrackedObject> trackedObjects = trackObjects();
                if(trackedObjects.size() > 0){
                    liDarWorkerTracker.setLastTrackedObjects(trackedObjects);
                    sendEvent(new TrackedObjectsEvent(getName(), trackedObjects, currentTick));
                }
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            //If the service that terminates was the time service, terminate too
                terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage -> terminate());    
    }

    //Goes over all events and completes the relevant ones
    private List<TrackedObject> trackObjects(){
        List<TrackedObject> trackedObjects = new ArrayList<>();

        //For every event the camera sent, we check if it needs to be processed based on the currentTick
        for(DetectObjectsEvent currentEvent : oldEvents){
            StampedDetectedObjects stampedDetectedObjects = currentEvent.getStampedDetectedObjects();
            //Checks if the event we process is relevant to our tick - check if its + or - here: page 15
            if(stampedDetectedObjects.getDetectionTime()+liDarWorkerTracker.getFrequency() <= currentTick){
                //For every object in the event, we create a TrackedObject and add it to the list
                for(DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()){
                    TrackedObject trackedObject = new TrackedObject(detectedObject.getID(), currentTick, detectedObject.getDescription(),
                                                    liDarDataBase.searchCoordinates(detectedObject, currentTick));
                    trackedObjects.add(trackedObject);
                }
                //Completes the event the camera sent
                complete(currentEvent, true);
            }
        }
        //removes processed events
        for (int i=0; i<oldEvents.size(); i++){
            if(oldEvents.get(i).getStampedDetectedObjects().getDetectionTime()+liDarWorkerTracker.getFrequency() <= currentTick){
                oldEvents.remove(i);
                i--;
            }
        }
        return trackedObjects;
    }
}
