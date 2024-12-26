package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import java.util.List;
import java.util.ArrayList;


//From Camera to LiDar worker
public class DetectObjectsEvent implements Event<DetectedObject> {
    private String senderName; //Which camera sent the event
    private List<DetectedObject> detectedObjects; //Which object needs to be detected
    private int tickTime; //time the object was detected

    public DetectObjectsEvent(String senderName, List<DetectedObject> detectedObjects, int tickTime){
        this.senderName = senderName;
        this.detectedObjects = detectedObjects;
        this.tickTime = tickTime;
    }

    public DetectObjectsEvent(String senderName, int tickTime){
        this.senderName = senderName;
        this.detectedObjects = new ArrayList<>();
        this.tickTime = tickTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public List<DetectedObject> getDetectedObject() {
        return detectedObjects;
    }

    public int getTickTime() {
        return tickTime;
    }
}
