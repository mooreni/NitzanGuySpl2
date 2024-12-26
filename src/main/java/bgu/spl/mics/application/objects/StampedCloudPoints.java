package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private List<CloudPoint> cloudPoints; //Is it supposed to be a list of list of doubles? page 17

    public StampedCloudPoints(String id, int time){
        this.id=id;
        this.time=time;
        this.cloudPoints=new ArrayList<>();
    }

    public StampedCloudPoints(String id, int time, List<CloudPoint> cloudPoints){
        this.id=id;
        this.time=time;
        this.cloudPoints=cloudPoints;
    }

    public String getID(){
        return id;
    }

    public int getTime(){
        return time;
    }

    public List<CloudPoint> getCoudPoints(){
        return cloudPoints;
    }
}
