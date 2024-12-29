package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currTime;
    private STATUS status;
    private List<Pose> poseList;

    public GPSIMU(int currTime, STATUS status, List<Pose> poseList){
        this.currTime = currTime;
        this.poseList = poseList;
        this.status = status;
    }

    public GPSIMU(){
        currTime = 0;
        poseList = new ArrayList<>();
        status = STATUS.DOWN;
    }

    public STATUS getStatus(){
        return status;
    }

    public int getTime(){
        return currTime;
    }

    public List<Pose> getPoseList(){
        return poseList;
    }


}
