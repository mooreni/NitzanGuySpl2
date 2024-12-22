package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.concurrent.Future;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Pose>{
    Future<Pose> future;

}
