package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.application.services.LiDarService;


/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    //I think these field makes sense to have for each camera?
    private LiDarService liDarService;
    private Thread thread;
    private String error = "LiDar sensor disconnected";

    public LiDarWorkerTracker(){
        this.id=0;
        this.frequency=0;
        this.status=STATUS.UP;
        this.lastTrackedObjects = new ArrayList<TrackedObject>();
        //I think thats how its meant to be done
        this.liDarService= new LiDarService(this);
        this.thread = new Thread(liDarService);
    }

    //Partial one - might be the only one needed
    public LiDarWorkerTracker (int id, int frequency){
        this.id=id;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.lastTrackedObjects = new ArrayList<TrackedObject>();
        //I think thats how its meant to be done
        this.liDarService= new LiDarService(this);
        this.thread = new Thread(liDarService);
    }

    public void startRunning(){
        System.out.println(thread.getName() + getSensorName());
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

    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> trackedObjects){
        this.lastTrackedObjects = trackedObjects;
    }

    public LiDarService getLiDarService(){
        return liDarService;
    }

    public void setStatus(STATUS status){
        this.status = status;
    }

    public String getSensorName(){
        return "LiDarWorkerTracker" + id;
    }

    public String getError(){
        return error;
    }
}
