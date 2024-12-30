package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int detectionTime;
    private List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int detectionTime){
        this.detectionTime=detectionTime;
        detectedObjects = new ArrayList<>();
    }

    public StampedDetectedObjects(int detectionTime, List<DetectedObject> detectedObjects){
        this.detectionTime=detectionTime;
        this.detectedObjects=detectedObjects;
    }

    public int getDetectionTime(){
        return detectionTime;
    }

    public List<DetectedObject> getDetectedObjects(){
        return detectedObjects;
    }

}
