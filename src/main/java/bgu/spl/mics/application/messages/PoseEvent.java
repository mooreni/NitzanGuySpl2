package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Pose;

//Pose might not be the correct object for the future!
public class PoseEvent implements Event<Pose>{
    Future<Pose> future;
    public Future<Pose> getFuture() {
        return future;
    }
}
