package org.tensorflow.lite.examples.detection;

import android.graphics.RectF;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Tracking {
    private static final int LATENCY_FRAME_RECOGNITION = 10;
    private static final int MIN_CONFIDENCE_NEW_OBJ = 95;
    private static final int MIN_CONFIDENCE_OBJ = 85;
    private static final int VAL_MIN = 100;
    private List<TrackedObject> trackedObjects;
    private int contID;
    private int progressiveFrame;

    public Tracking(){
        this.trackedObjects = new ArrayList<TrackedObject>();
        this.contID=0;
        this.progressiveFrame=0;
    }

    private double calculateDistanceBetweenPointsWithHypot(
            float x1,
            float y1,
            float x2,
            float y2) {

        float ac = Math.abs(y2 - y1);
        float cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }
    public void update(List<Detector.Recognition> object_rect){
        List<Detector.Recognition> new_object_rect=new ArrayList<Detector.Recognition>();

        //FILTERING
        for (Detector.Recognition obj_rect: object_rect){
            if (obj_rect.getConfidence()>MIN_CONFIDENCE_OBJ) new_object_rect.add(obj_rect);
        }

        //creation matrix using guava table
        Table<Detector.Recognition, TrackedObject, Double> distanceTable = HashBasedTable.create();
        for (Detector.Recognition new_object: new_object_rect){
            for (TrackedObject trackedObject: trackedObjects){
                RectF rect=new_object.getLocation();
                float cx1=rect.centerX();
                float cy1=rect.centerY();
                float cx2=trackedObject.getTrackedRecord().get(trackedObject.getTrackedRecord().size() - 1).getCx();
                float cy2=trackedObject.getTrackedRecord().get(trackedObject.getTrackedRecord().size() - 1).getCx();
                distanceTable.put(new_object,trackedObject,Double.valueOf(calculateDistanceBetweenPointsWithHypot(cx1,cy1,cx2,cy2)));
            }
        }

        boolean onlyDistanceObj=false;

        while(!onlyDistanceObj || distanceTable.isEmpty()){
            onlyDistanceObj=true;
            //algorithm new_object min distance
            for (Detector.Recognition new_object: distanceTable.rowKeySet()){
                Map<TrackedObject,Double> mapRow=distanceTable.row(new_object);
                Double minRow = Collections.min(mapRow.values());

                if (minRow<VAL_MIN){
                    onlyDistanceObj=false;
                    for (TrackedObject trackedObject: mapRow.keySet()){
                        if (mapRow.get(trackedObject)==minRow){
                            //found trackedObject
                            Map<Detector.Recognition ,Double> mapColumn=distanceTable.column(trackedObject);
                            Double minColumn = Collections.min(mapColumn.values());

                            for (Detector.Recognition new_obj:mapColumn.keySet()){
                                if (mapColumn.get(new_obj)==minColumn){

                                    if (new_obj.equals(new_object)){
                                        //found corrispondance
                                        Point tmp =new Point(new_obj.getLocation().centerX(),new_obj.getLocation().centerY());
                                        trackedObject.setTrackedRecord(tmp);
                                        //delete distance
                                        distanceTable.row(new_obj).clear();
                                        distanceTable.column(trackedObject).clear();
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        //Add new object
        for (Detector.Recognition new_object: distanceTable.rowKeySet()){
            if (new_object.getConfidence()>MIN_CONFIDENCE_NEW_OBJ){
                Point tmp =new Point(new_object.getLocation().centerX(),new_object.getLocation().centerY());
                TrackedObject to=new TrackedObject(String.valueOf(contID));
                contID++;
                to.setTrackedRecord(tmp);
                trackedObjects.add(to);
            }
        }

        //Check last update tracked object
        for(TrackedObject to: trackedObjects){
            if (progressiveFrame-to.getCont()>LATENCY_FRAME_RECOGNITION){
                trackedObjects.remove(to);
            }
        }

    }
}
