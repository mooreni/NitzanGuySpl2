package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService" + camera.getID());
        this.camera = camera;
        // TODO Implement this - do we need to add something else?
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if(camera.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                int currentTick = tickMessage.getTickTime();
                for(StampedDetectedObjects obj : camera.getStampedDetectedObjects()){
                    if(obj.getTime() == currentTick+camera.getFrequency()){
                        sendEvent(new DetectObjectsEvent(getName(), obj.getDetectedObjects(), currentTick));
                    }
                }
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            //If the service that terminates was the time service, terminate too
            if(terminateMessage.getSenderName().compareTo("TimeService") ==0){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });
        //If one of the services crashed, terminate too
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage -> terminate());
    }
}
