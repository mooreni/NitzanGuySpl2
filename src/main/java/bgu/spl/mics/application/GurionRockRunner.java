package bgu.spl.mics.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
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
        LiDarDataBase.LoadData(lidarDataPath.toString());
        GPSIMU gpsimu = new GPSIMU(poseDataPath.toString());
        FusionSlam.setOutputPath(configDirectory.toString());
        FusionSlam.getInstance().setSensorsCount(cameras.size() + lidarWorkers.size() + 1);

        //Initialize the services
        CountDownLatch latch = new CountDownLatch(cameras.size() + lidarWorkers.size() + 2);

        TimeService timeService = new TimeService(config.getTickTime() * 1000, config.getDuration());
        PoseService poseService = new PoseService(gpsimu);
        poseService.setLatch(latch);
        FusionSlamService fusionSlamService = new FusionSlamService();
        fusionSlamService.setLatch(latch);

        System.out.println(latch.getCount());

        for(Camera camera: cameras){
            CameraService camService = new CameraService(camera, latch);
            Thread t = new Thread(camService);
            t.start();
            System.out.println(latch.getCount());

        }
        System.out.println(latch.getCount());

        for(LiDarWorkerTracker lidarWorker: lidarWorkers){
            LiDarService liDarService = new LiDarService(lidarWorker, latch);
            Thread t = new Thread(liDarService);
            t.start();
            System.out.println(latch.getCount());

        }
        Thread t1 = new Thread(poseService);
        System.out.println(t1.getName() + "poseService");
        t1.start();
        System.out.println(latch.getCount());



        Thread t2 = new Thread(fusionSlamService);
        System.out.println(t2.getName() + "fusionService");
        t2.start();

        System.out.println(latch.getCount());
        try{
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(latch.getCount());

        Thread t3 = new Thread(timeService);
        System.out.println(t3.getName() + "timeService");
        t3.start();

    }

    
}
