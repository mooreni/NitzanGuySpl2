package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class LiDARDataBaseHolder {
		private static volatile LiDarDataBase instance = new LiDarDataBase();
	}

    private List<StampedCloudPoints> cloudPoints;
    private List<TrackedObject> lastTrackedObjects;

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private LiDarDataBase(){
        cloudPoints = new ArrayList<StampedCloudPoints>();
        lastTrackedObjects = new ArrayList<TrackedObject>();

    }

    private void LoadData(String filePath){
        Gson gson = new Gson();
        List<StampedCloudPoints> parsedCloudPoints = null;
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type for the list
            Type stampedCloudPointType = new TypeToken<List<StampedCloudPoints>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            parsedCloudPoints = gson.fromJson(reader,stampedCloudPointType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudPoints = parsedCloudPoints;
    }
        // Should load the lidar data file and extract the cloud points to the list

    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = LiDARDataBaseHolder.instance;
        instance.LoadData(filePath);
        return instance;
    }

    public static LiDarDataBase getInstance() {
        return LiDARDataBaseHolder.instance;
    }


    public List<StampedCloudPoints> getStampedCloudPoints(){
        return cloudPoints;
    } 

    public List<CloudPoint> searchCoordinates(DetectedObject obj, int time){ // Should return the object's coordinates
        List<CloudPoint> coordinates = new ArrayList<CloudPoint>();
        for(StampedCloudPoints stampedCloudPoints : cloudPoints){
            if(stampedCloudPoints.getTime() == time && stampedCloudPoints.getId().compareTo(obj.getID()) == 0){
                coordinates = stampedCloudPoints.getCloudPointsAsObj();
            }
        }
        return coordinates;
    }

    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> trackedObjects){
        lastTrackedObjects = trackedObjects;
    }
}
