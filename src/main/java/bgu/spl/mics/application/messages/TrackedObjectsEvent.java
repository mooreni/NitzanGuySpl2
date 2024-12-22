package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.LandMark;

import java.util.concurrent.Future;

public class TrackedObjectsEvent implements Event<LandMark> {
    Future<LandMark> future;
}
