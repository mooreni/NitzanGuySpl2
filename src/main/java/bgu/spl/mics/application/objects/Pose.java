package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {

    private Double x;
    private Double y;
    private Double yaw;
    private int timeOfPose;

    public Pose(Double x, Double y, Double yaw, int time){
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.timeOfPose = time;
    }

    public Pose(Double x, Double y, Double yaw){
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.timeOfPose = 0;
    }

    public void setTime(int time){
        this.timeOfPose = time;
    }

    public Double getX(){
        return x;
    }

    public Double getY(){
        return y;
    }

    public Double getYaw(){
        return yaw;
    }

    public int getTime(){
        return timeOfPose;
    }
}
