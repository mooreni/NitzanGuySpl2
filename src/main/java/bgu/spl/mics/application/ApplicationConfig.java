package bgu.spl.mics.application;

import java.util.List;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;


public class ApplicationConfig {
    CamerasConfigurations Cameras;
    LidarConfigurations LidarWorkers;
    String poseJsonFile;
    int TickTime;
    int Duration;

    public ApplicationConfig(){
        Cameras = new CamerasConfigurations();
        LidarWorkers = new LidarConfigurations();
    }

    public CamerasConfigurations getCamerasConfigurations(){
        return Cameras;
    }

    public LidarConfigurations getLidarConfigurations(){
        return LidarWorkers;
    }   

    public String getPoseJsonFile(){
        return poseJsonFile;
    }

    public int getTickTime(){
        return TickTime;
    }

    public int getDuration(){
        return Duration;
    }

    public void updateAll(){
        Cameras.updateAll();
    }

    public class CamerasConfigurations{
        List<Camera> CamerasConfigurations;
        String camera_datas_path;

        public CamerasConfigurations(){
            CamerasConfigurations = null;
            camera_datas_path = null;
        }

        public List<Camera> getCamerasConfigurations(){
            return CamerasConfigurations;
        }

        public String getCameraDatasPath(){
            return camera_datas_path;
        }

        public void updateAll(){
            for(Camera camera : CamerasConfigurations){
                camera.loadData(camera_datas_path);
            }
        }
    }

    public class LidarConfigurations{
        List<LiDarWorkerTracker> LidarConfigurations;
        String lidars_data_path;

        public LidarConfigurations(){
            LidarConfigurations = null;
            lidars_data_path = null;
        }

        public List<LiDarWorkerTracker> getLidarConfigurations(){
            return LidarConfigurations;
        }   

        public String getLidarsDataPath(){
            return lidars_data_path;
        }

    }
}

