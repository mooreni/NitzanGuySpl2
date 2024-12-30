package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CameraService;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> stampedDetectedObjects;

    //I think these field makes sense to have for each camera?
    private CameraService cameraService;
    private Thread thread;

    //Partial one - might be the only one needed
    public Camera (int id, int frequency, String filePath){
        this.id=id;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        loadData(filePath);
        //I think thats how its meant to be done
        this.cameraService= new CameraService(this);
        this.thread = new Thread(cameraService);
        thread.start();
    }

    //Full constructor - with status
    public Camera (int id, int frequency, String filePath, STATUS status){
        this.id=id;
        this.frequency=frequency;
        this.status=status;
        loadData(filePath);
        //I think thats how its meant to be done
        this.cameraService= new CameraService(this);
        this.thread = new Thread(cameraService);
        thread.start(); 
    }

    public int getID(){
        return id;
    }

    public int getFrequency(){
        return frequency;
    }

    public STATUS getStatus(){
        return status;
    }

    public List<StampedDetectedObjects> getStampedDetectedObjects(){
        return stampedDetectedObjects;
    }

    public CameraService getCameraService(){
        return cameraService;
    }

    private void loadData(String filePath){
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type for the list
            Type detectedType = new TypeToken<List<StampedDetectedObjects>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            stampedDetectedObjects = gson.fromJson(reader,detectedType);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
