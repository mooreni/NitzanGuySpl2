package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.security.auth.login.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import bgu.spl.mics.application.ApplicationConfig;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


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

        //ApplicationConfig config = parseConfigurationFile(args[0]);
        //config.updateAll();
        //System.out.println(config.Cameras.CamerasConfigurations.get(0).getFrequency());
        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }

    private static ApplicationConfig parseConfigurationFile(String path) {
        Gson gson = new Gson();
        ApplicationConfig config = null;
        try (FileReader reader = new FileReader(path)) {
            // Define the type for the list
            Type configType = new TypeToken<ApplicationConfig>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            config = gson.fromJson(reader,configType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
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
