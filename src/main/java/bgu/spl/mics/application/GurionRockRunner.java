package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        MessageBusImpl bus = MessageBusImpl.getInstance();
        bus.subscribeBroadcast(TickBroadcast.class, new TimeService(500, 3));

        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}

/*Project to do and updates list

-Messages: Complete, there might be some extra fields that we wont need

-Objects:
    Complete:
        Camera related: DetectedObject, StampedDetectObject, Camera
        LiDar related: CloudPoint, TrackedObject, LiDarWorkerTracker
    Need to look into:
        StampedCloudPoints - Unsure if the list in line 13 is a list<cloud points>, or list<list<doubles>> - page 17 of the assignment
    ================================
    Didnt touch: needs work
        Fusion related: Landmark, FusionSLAM
        Pose related: Pose, GPSIMU
        Statistical Folder
        LiDARDataBase

-Services:
    TimeService: Mostly done, just need to make sure what happens once it terminates - line 41-42: send something?
    CameraService: Mostly done. Need to figure out lines 47,49
    ===============================
    LiDarService: started working. Lines 57-64 need to have the whole handling of the DetectObjectEvent.
                                                                        Unsure what goes on with the dataBase there.
    ===============================
    FusionSlamService: Didnt touch, only added the relevent registers
    PoseService: Didnt touch, only added the relevent registers
    ErrorHandling (page 21): didnt do

More changes and to do:
- changed a little in the 'run' method of microService: added unregister and broadcast
*/
