package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.concurrent.Future;
import bgu.spl.mics.application.objects.CloudPoint;

public class DetectObjectsEvent implements Event<CloudPoint> {
    Future<CloudPoint> future;
    
    
}
