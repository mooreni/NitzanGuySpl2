package bgu.spl.mics.application;

import java.util.List;
import bgu.spl.mics.application.objects.*;

public class ErrorOutput extends NormalOutput {
    //Should represent a file ready to be written into an output file for a crashed-output
    //Used for gson - it will take each field and write it into a json file

    //Notes that might help - we might need to change the crashed broadcast so that it can have more info on who crashed, 
    //so that when fusion recieves it, it can write it as an output file
    
    private String error;
    private String faultySensor;
    private List<Object> lastFrames;
    private List<Pose> poses;

    public ErrorOutput(int systemRuntime, int numDetectedObjects, int numTrackedObjects,int numLandmarks, 
                        List<LandMark> landMarks ,String error, String faultySensor, List<Object> lastFrames, List<Pose> poses){
        super(systemRuntime, numDetectedObjects, numTrackedObjects, numLandmarks, landMarks);
        this.error = error;
        this.faultySensor = faultySensor;
        this.lastFrames = lastFrames;
        this.poses = poses;
    }

}
