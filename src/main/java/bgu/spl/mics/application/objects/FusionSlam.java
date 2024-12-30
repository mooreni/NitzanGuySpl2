package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    private List<LandMark> globalMap;
    private List<Pose> previousRobotPoses;
    /**
     * Retrieves the single instance of FusionSlam.
     *
     * @return The FusionSlam instance.
     */ 
    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    /**
     * Initializes the FusionSlam object.
     */
    private FusionSlam() {
        previousRobotPoses = new ArrayList<>();
        globalMap = new ArrayList<>();

    }

    /**
     * Updates the global map with new landmarks.
     *
     * @param landmarks The landmarks to add to the global map.
     */ 
    public void updateGlobalMap(LandMark landmarks) {
        globalMap.add(landmarks);
    }

    public void updatePreviousRobotPoses(Pose pose) {
        previousRobotPoses.add(pose);
    }

    public CloudPoint calculateCoordinates(Pose pose, CloudPoint cloudPoint) {
        // Step 1: Convert yaw from degrees to radians
        double yawRadians = Math.toRadians(pose.getYaw());  // Convert to radians
        
        // Step 2: Compute cosine and sine of the yaw angle
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);
        
        // Step 3: Apply the transformation
        double xGlobal = cosTheta * cloudPoint.getX() - sinTheta * cloudPoint.getY() + pose.getX();
        double yGlobal = sinTheta * cloudPoint.getX() + cosTheta * cloudPoint.getY() + pose.getY();
        
        return new CloudPoint(xGlobal, yGlobal);
    }

    public List<LandMark> getGlobalMap() {
        return globalMap;
    }
    public List<Pose> getPreviousRobotPoses() {
        return previousRobotPoses;
    }
    
    public List<CloudPoint> averageCloudPoints(List<CloudPoint> list1, List<CloudPoint> list2) {
        // Check if the lists are of the same length
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("Both lists must have the same length.");
        }

        // Create a new list to store the averaged CloudPoints
        List<CloudPoint> averagedList = new ArrayList<>();

        // Iterate through both lists, average the corresponding points, and add to the result list
        for (int i = 0; i < list1.size(); i++) {
            CloudPoint point1 = list1.get(i);
            CloudPoint point2 = list2.get(i);

            // Calculate the average x and y coordinates
            Double avgX = (point1.getX() + point2.getX()) / 2;
            Double avgY = (point1.getY() + point2.getY()) / 2;

            // Add the averaged point to the result list
            averagedList.add(new CloudPoint(avgX, avgY));
        }

        return averagedList;
    }
}
