package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;
    private int currentTick;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
        currentTick=0;
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
                // Before crashing, sends to the fusionSlam the last stamped detected objects
                FusionSlam.getInstance().getLastFrames().add(camera.getLastStampedDetectedObjects());
                sendBroadcast(new CrashedBroadcast(getName(), camera.getError(), camera.getCameraKey()));
                terminate();
            }
            else{
                currentTick = tickMessage.getTickTime();
                for(StampedDetectedObjects obj : camera.getStampedDetectedObjects()){
                    //If there is Error in currentTick
                    if(obj.getDetectionTime() == currentTick){
                        for(DetectedObject detectedObject : obj.getDetectedObjects()){
                            if(detectedObject.getID().equals("ERROR")){
                                System.out.println("CameraService: Error detected");
                                camera.setStatus(STATUS.ERROR);
                                camera.setError(detectedObject.getDescription());
                                // Before crashing, sends to the fusionSlam the last stamped detected objects
                                FusionSlam.getInstance().getLastFrames().add(camera.getLastStampedDetectedObjects());
                                System.out.println("CameraService: sent last frames to fusionSlam");
                                sendBroadcast(new CrashedBroadcast(getName(), camera.getError(), camera.getCameraKey()));
                                System.out.println("CameraService: sent broadcast");
                                terminate();
                                System.out.println("CameraService: terminated");
                                return;
                            }
                        }
                    }

                    //If there is data from time T, we will send it when we get to T+Frequency
                    if(obj.getDetectionTime()+camera.getFrequency() == currentTick){
                        //Send event gets back a future - do we need to do something with it?
                        System.out.println("CameraService:" + camera.getSentObjectsCount());
                        sendEvent(new DetectObjectsEvent(getName(), obj, currentTick));
                        camera.setLastStampedDetectedObjects(obj); // Save the last stamped detected objects
                        camera.increaseSentObjectsCount();
                        StatisticalFolder.getInstance().increaseNumDetectedObjects(obj.getDetectedObjects().size());
                    }
                }
                //If we sent all the objects, terminate
                if (camera.getSentObjectsCount() == camera.getStampedDetectedObjects().size()) {
                    camera.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminatedBroadcast(getName()));
                    terminate();
                }
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            if((terminateMessage.getSenderName().compareTo("TimeService") ==0) ||
                (terminateMessage.getSenderName().compareTo("FusionSlam") ==0)){
                terminate();
            }
        });
        //If one of the services crashed, terminate too
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{
            // Before crashing, sends to the fusionSlam the last stamped detected objects
            FusionSlam.getInstance().getLastFrames().add(camera.getLastStampedDetectedObjects());
            sendBroadcast(new CrashedBroadcast(getName(), camera.getError(), camera.getCameraKey()));
            terminate();
        });
    }
}
