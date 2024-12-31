package bgu.spl.mics.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.MessageBusImpl;


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

        Path configAbsolutePath = Paths.get(args[0]).toAbsolutePath();
        ApplicationConfig config = ParserConfig.parseConfig(configAbsolutePath.toString());

        //Setup the paths
        Path configDirectory = configAbsolutePath.getParent();
        String lidarDataRelativePath = config.getLidarWorkers().getLidarsDataPath();
        Path lidarDataPath = configDirectory.resolve(lidarDataRelativePath).normalize();
        String camerasDataRelativePath = config.getCameras().getCameraDatasPath();
        Path camerasDataPath = configDirectory.resolve(camerasDataRelativePath).normalize();
        String poseDataRelativePath = config.getPoseJsonFile();
        Path poseDataPath = configDirectory.resolve(poseDataRelativePath).normalize();
        config.getCameras().setFilePath(camerasDataPath.toString());
        config.updateCameras();


        //Initialize the objects
        List<Camera> cameras = config.getCameras().getCamerasConfigurations();
        List<LiDarWorkerTracker> lidarWorkers = config.getLidarWorkers().getLidarConfigurations();
        LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath.toString());
        GPSIMU gpsimu = new GPSIMU(poseDataPath.toString());
        FusionSlam fusionSlam = FusionSlam.getInstance();
        StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

        //Initialize the services
        TimeService timeService = new TimeService(config.getTickTime() * 1000, config.getDuration());
        PoseService poseService = new PoseService(gpsimu);
        FusionSlamService fusionSlamService = new FusionSlamService();

        MessageBusImpl messageBus = MessageBusImpl.getInstance();

        // TODO: Start the simulation.
    }

    
}

/*Project to do and updates list

-Services:
    TimeService: Mostly done, just need to make sure what happens once it terminates - line 41-42: send something?
                           Check if time is in milliseconds or seconds

    CameraService: Mostly done. Need to figure out lines 47,49

    LiDarService: line 63: is it <= or ==?               
    
    FusionSlamService: 

    PoseService: 

- ErrorHandling (page 21): didnt do

More changes and to do:
*/


/* Notes on lidar worker process
We need to do several things here.
1. For every event, we need to go over obj.getDetectedObjects() and do ____ with it and the dataBase
    a. the database should be read from a lidar_data.json file thats given to us, like in the examples folder
2. with the result of this operation, we need to:
    a. send a TrackedObjectEvent to fusionSLAM
        -SendEvent gets back a future - do we need to do something with it?
    b. save the results into lastTrackedObjects in LiDarWorkerTracker - a field of the last objects that were tracked
3. Complete the event we just solved - the WDetectObjectsEvent. We need to do a complete to it
4. save the results into the dataBase
*/
