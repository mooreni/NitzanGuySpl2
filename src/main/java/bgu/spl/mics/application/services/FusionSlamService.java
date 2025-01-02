package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    private boolean didTimeTerminate;
    private String error;
    private String faultySensor;        
    private CountDownLatch latch;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService() {
        super("FusionSlam");
        this.fusionSlam = FusionSlam.getInstance();
        didTimeTerminate = false;
        error = "";
        faultySensor = "";
    }


    public void setLatch(CountDownLatch latch){
        this.latch = latch;
    }

    

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            //If everyone finished except timeService, notify timeService to finish
            if (fusionSlam.getSensorsCount()==0){
                sendBroadcast(new TerminatedBroadcast(getName()));
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
            System.out.println("FusionSlamService: PoseEvent received at tick " + pose.getTime());
            boolean exists = false;
            for(int i = 0; i < fusionSlam.getWaitingTrackedObjects().size()&&!exists; i++){
                if(fusionSlam.getWaitingTrackedObjects().get(i).getTime() == pose.getTime()){
                    fusionSlam.updateGlobalMap(fusionSlam.getWaitingTrackedObjects().get(i));
                    System.out.println("FusionSlamService: Landmark uploaded at tick " + pose.getTime());
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

            if(terminateMessage.getSenderName().compareTo("TimeService") == 0){
                didTimeTerminate = true;
            }

            //In case everyone finished, including timeService, terminate and output
            if(didTimeTerminate==true && fusionSlam.getSensorsCount() == 0){
                terminate();
                fusionSlam.createNormalOutput();
            }

            //If everyone finished except timeService, notify timeService to finish
            else if(didTimeTerminate==false && fusionSlam.getSensorsCount() == 0){
                sendBroadcast(new TerminatedBroadcast(getName()));
            }

            //Else: if the time service finished, the rest will recieve the broadcast from timeService and finish too eventually
            //      if the time service didnt finish and only some finished too, continue as well
        });

        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{
            if((crashedMessage.getSenderName().compareTo("CameraService") == 0)||
            (crashedMessage.getSenderName().compareTo("LiDarService") == 0)||
            (crashedMessage.getSenderName().compareTo("Pose") == 0)){
                fusionSlam.decrementSensorsCount();
            }
            System.out.println("FusionSlamService: " + crashedMessage.getSenderName() + " crashed");
            System.out.println("FusionSlamService: " + fusionSlam.getSensorsCount() + " sensors left");

            // Sets the error message and faulty sensor for the first crash
            if(error.compareTo("") == 0){
                error = crashedMessage.getError();
                faultySensor = crashedMessage.getFaultySensor();
                System.out.println("FusionSlamService: Error detected in " + faultySensor);
            }
            // Should we regard the time service?
            // Waits for everyone to crash, then creates the error output and terminates
            if(fusionSlam.getSensorsCount() == 0){
                System.out.println("FusionSlamService: All sensors crashed");
                fusionSlam.createErrorOutput(error, faultySensor, fusionSlam.getLastFrames());
                System.out.println("FusionSlamService: Error output created");
                terminate();
                System.out.println("FusionSlamService: Terminated");
            }
        });    
        latch.countDown();

    }
}
