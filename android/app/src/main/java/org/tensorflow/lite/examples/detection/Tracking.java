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
    private static final float REMOVE_AFTER_N_FRAMES = 15;
    private static final float MIN_CONFIDENCE_NEW_OBJ = 0.95f;
    private static final float MIN_CONFIDENCE_OBJ = 0.85f;
    private static final float VAL_MIN = 100;
    private List<TrackedObject> trackedObjects;
    private int contID;
    private int progressiveFrame;

    public Tracking(){
        this.trackedObjects = new ArrayList<TrackedObject>();
        this.contID=1;
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

    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    public List<Detector.Recognition> update(List<Detector.Recognition> object_rect){
        List<Detector.Recognition> out_obj_rect=new ArrayList<Detector.Recognition>();
        List<Detector.Recognition> new_object_rect=new ArrayList<Detector.Recognition>();

        //FILTERING
        for (Detector.Recognition obj_rect: object_rect){
            if (obj_rect.getConfidence()>MIN_CONFIDENCE_OBJ) new_object_rect.add(obj_rect);
        }

        //creation matrix using guava table
        Table<Detector.Recognition, TrackedObject, Double> distanceTable = HashBasedTable.create();
        List<Detector.Recognition> remainingNewObjects = new ArrayList<>(); //these are the same objects labeling each row of the table, but these remain in the list event when no trackedObjects is left on the table!
        for (Detector.Recognition new_object: new_object_rect){
            remainingNewObjects.add(new_object);
            for (TrackedObject trackedObject: trackedObjects){
                RectF rect=new_object.getLocation();
                float cx1=rect.centerX();
                float cy1=rect.centerY();
                float cx2=trackedObject.getPointHistory().get(trackedObject.getPointHistory().size() - 1).getCx();
                float cy2=trackedObject.getPointHistory().get(trackedObject.getPointHistory().size() - 1).getCy();
                distanceTable.put(new_object,trackedObject,Double.valueOf(calculateDistanceBetweenPointsWithHypot(cx1,cy1,cx2,cy2)));
            }
        }

        boolean onlyDistantObj=false;

        while(!onlyDistantObj && !distanceTable.isEmpty()){ //repeat the process until no new_object or no trackedObject element is remained, or if elements are too distant
            Table<Detector.Recognition, TrackedObject, Double> stepDistanceTable = HashBasedTable.create(distanceTable);
            onlyDistantObj=true;
            //algorithm new_object min distance
            for (Detector.Recognition new_object: stepDistanceTable.rowKeySet()){
                Map<TrackedObject,Double> mapRow=stepDistanceTable.row(new_object);
                Double minRow = Collections.min(mapRow.values());

                if (minRow<VAL_MIN){
                    onlyDistantObj=false;
                    for (TrackedObject trackedObject: mapRow.keySet()){
                        if (minRow.equals(mapRow.get(trackedObject))){
                            //found trackedObject
                            Map<Detector.Recognition ,Double> mapColumn=stepDistanceTable.column(trackedObject);
                            Double minColumn = Collections.min(mapColumn.values());

                            for (Detector.Recognition new_obj:mapColumn.keySet()){
                                if (minColumn.equals(mapRow.get(trackedObject))){

                                    if (new_obj.equals(new_object)){
                                        //found corrispondance
                                        Point tmp = new Point(new_obj.getLocation().centerX(),new_obj.getLocation().centerY());
                                        trackedObject.setNewPoint(tmp, progressiveFrame);
                                        out_obj_rect.add(new Detector.Recognition(trackedObject.getId(), new_object.getTitle(), new_object.getConfidence(), new_object.getLocation()));
                                        //delete distance
                                        distanceTable.row(new_obj).clear();
                                        distanceTable.column(trackedObject).clear();
                                        remainingNewObjects.remove(new_obj);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        //Add remaining new object
        for (Detector.Recognition new_object: remainingNewObjects){
            if (new_object.getConfidence()>MIN_CONFIDENCE_NEW_OBJ){
                Point tmp =new Point(new_object.getLocation().centerX(),new_object.getLocation().centerY());
                TrackedObject to=new TrackedObject(String.valueOf(contID));
                contID++;
                to.setNewPoint(tmp, progressiveFrame);
                trackedObjects.add(to);
                out_obj_rect.add(new Detector.Recognition(to.getId(), new_object.getTitle(), new_object.getConfidence(), new_object.getLocation()));
            }
        }

        //Check last update tracked object
        for(TrackedObject to: new ArrayList<>(trackedObjects)){
            if (progressiveFrame-to.getLastUpdateFrameNum()> REMOVE_AFTER_N_FRAMES){
                trackedObjects.remove(to);
            }
        }
        progressiveFrame++;

        return out_obj_rect;
    }
}
