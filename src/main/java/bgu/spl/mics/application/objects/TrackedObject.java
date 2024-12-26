package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private List<CloudPoint> coordinates;

    public TrackedObject(String id, int time){
        this.id=id;
        this.time=time;
        this.description="";
        this.coordinates=new ArrayList<>();    
    }

    public TrackedObject(String id, int time, String description, List<CloudPoint> coordinates){
        this.id=id;
        this.time=time;
        this.description=description;
        this.coordinates=coordinates;
    }

    public String getID(){
        return id;
    }

    public int getTime(){
        return time;
    }

    public String getDescription(){
        return description;
    }

    public List<CloudPoint> getCoudCoordinates(){
        return coordinates;
    }
}
