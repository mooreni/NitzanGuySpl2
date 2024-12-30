package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.services.CameraService;

import java.util.List;
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
    public Camera (int id, int frequency){
        this.id=id;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        //I think thats how its meant to be done
        this.cameraService= new CameraService(this);
        this.thread = new Thread(cameraService);
        thread.start();
    }

    //Full constructor - all fields
    public Camera (int id, int frequency, STATUS status, List<StampedDetectedObjects> stampedDetectedObjects){
        this.id=id;
        this.frequency=frequency;
        this.status=status;
        this.stampedDetectedObjects=stampedDetectedObjects;
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
}
