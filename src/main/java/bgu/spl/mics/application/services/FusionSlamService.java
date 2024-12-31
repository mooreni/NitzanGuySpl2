package bgu.spl.mics.application.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.objects.LandMark;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService() {
        super("FusionSlam");
        this.fusionSlam = FusionSlam.getInstance();
        // TODO Implement this - do we need to add something else?
    }

    

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if (fusionSlam.getSensorsCount()==0){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
            }
        });
        
        subscribeEvent(TrackedObjectsEvent.class, trackedObjectMessage ->{
            List<TrackedObject> trackedObjects = trackedObjectMessage.getTrackedObject();
            for(TrackedObject trackedObject : trackedObjects){
                if(fusionSlam.getPreviousRobotPoses().size() >= trackedObject.getTime()){
                    fusionSlam.updateGlobalMap(trackedObject);
                }
                else{
                    fusionSlam.addWaitingTrackedObject(trackedObject);
                }
            }
            complete(trackedObjectMessage, true);
        });

        subscribeEvent(PoseEvent.class, poseMessage ->{
            Pose pose = poseMessage.getPose();
            fusionSlam.addPose(pose);
            boolean exists = false;
            for(int i = 0; i < fusionSlam.getWaitingTrackedObjects().size()&&!exists; i++){
                if(fusionSlam.getWaitingTrackedObjects().get(i).getTime() == pose.getTime()){
                    fusionSlam.updateGlobalMap(fusionSlam.getWaitingTrackedObjects().get(i));
                    fusionSlam.getWaitingTrackedObjects().remove(i);
                    exists = true;
                }
            }
            complete(poseMessage, true);
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            if((terminateMessage.getSenderName().compareTo("CameraService") == 0)||
                (terminateMessage.getSenderName().compareTo("LiDarService") == 0)||
                (terminateMessage.getSenderName().compareTo("Pose") == 0)){
                fusionSlam.decrementSensorsCount();
            }
            if(fusionSlam.getSensorsCount() == 0){
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
                fusionSlam.createNormalOutput();
            }

        });

        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{
            terminate();
        });    
    }
}
