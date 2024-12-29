package bgu.spl.mics.application.services;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Pose");
        this.gpsimu = gpsimu;
        // TODO Implement this - do we need to add something else?
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
           if(gpsimu.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                int currentTick = tickMessage.getTickTime();
                List<Pose> poseList = gpsimu.getPoseList();
                sendEvent(new PoseEvent(getName(), poseList.get(currentTick-1), currentTick));
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage -> terminate());
    }
}
