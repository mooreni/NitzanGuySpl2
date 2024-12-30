package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currTime;
    private STATUS status;
    private List<Pose> poseList;

    public GPSIMU(int currTime, STATUS status, List<Pose> poseList, String filePath){
        this.currTime = currTime;
        this.poseList = poseList;
        this.status = status;
        this.loadData(filePath);
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

    private void loadData(String filePath){
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type for the list
            Type poseType = new TypeToken<List<Pose>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            poseList = gson.fromJson(reader,poseType);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
