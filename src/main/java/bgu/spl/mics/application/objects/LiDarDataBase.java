package bgu.spl.mics.application.objects;

import java.util.ArrayList;
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
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private LiDarDataBase(){
        cloudPoints = new ArrayList<StampedCloudPoints>();
    }

    private static void LoadData(String filePath){
        // Should load the lidar data file and extract the cloud points to the list
    }

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

    public List<CloudPoint> searchCoordinates(DetectedObject obj){ // Should return the object's coordinates
        return null;
    }
}
