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
        //FusionSlam fusionSlam = new FusionSlam();
        StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

        //Initialize the services
        TimeService timeService = new TimeService(config.getTickTime(), config.getDuration());
        PoseService poseService = new PoseService(gpsimu);
        //FusionSlamService fusionSlamService = new FusionSlamService();

        MessageBusImpl messageBus = MessageBusImpl.getInstance();

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
                                                                        line 63: is it <= or ==?               
    ===============================
    FusionSlamService: Didnt touch, only added the relevent registers
    PoseService: Didnt touch, only added the relevent registers
    ErrorHandling (page 21): didnt do

More changes and to do:
- changed a little in the 'run' method of microService: added unregister and broadcast
*/
