package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.CloudPoint;


//CloudPoint might not be the correct object for the future!
public class DetectObjectsEvent implements Event<CloudPoint> {
    Future<CloudPoint> future;
    
    public Future<CloudPoint> getFuture() {
        return future;
    }
}
