package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

//From LiDar to Fusion-SLAM
//Page 19 - is it tracked object or list of tracked objects?
public class TrackedObjectsEvent implements Event<List<TrackedObject>> {
    private String senderName; //Which camera sent the event
    private List<TrackedObject> trackedObject; //Which object needs to be detected
    private int tickTime; //<==============do we need this?

    public TrackedObjectsEvent(String senderName, List<TrackedObject> trackedObject, int tickTime){
        this.senderName = senderName;
        this.trackedObject = trackedObject;
        this.tickTime = tickTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public List<TrackedObject> getTrackedObject() {
        return trackedObject;
    }

    public int getTickTime() {
        return tickTime;
    }
}
