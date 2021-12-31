package org.tensorflow.lite.examples.detection;

import android.graphics.RectF;

import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.util.ArrayList;
import java.util.List;

public class Tracking {
    private List<Point> center_points;
    private int id_count = 0;

    public Tracking(){
        this.center_points= new ArrayList<Point>();
        this.id_count = 0;
    }

    public double calculateDistanceBetweenPointsWithHypot(
            float x1,
            float y1,
            float x2,
            float y2) {

        float ac = Math.abs(y2 - y1);
        float cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }
    public List<Detector.Recognition> update(List<Detector.Recognition> object_rect){
        List<Detector.Recognition> boxes=new ArrayList<Detector.Recognition>();
        // Get center point of new object
        for(Detector.Recognition obj: object_rect){
            RectF rect=obj.getLocation();
            float cx=rect.centerX();
            float cy=rect.centerY();
            //Find out if that object was detected already
            boolean same_object_detected=false;
            for (int i=0;i<center_points.size();i++){
                double dist=calculateDistanceBetweenPointsWithHypot(cx,cy,
                        center_points.get(i).getCx(),center_points.get(i).getCy());
                if (dist<25){
                    center_points.get(i).setCx(cx);
                    center_points.get(i).setCy(cy);

                    boxes.add(new Detector.Recognition(center_points.get(i).getId(),obj.getTitle(),obj.getConfidence(), obj.getLocation()));
                    same_object_detected=true;
                    break;
                }
            }
            //New object is detected
            if (same_object_detected==true){
                center_points.add(new Point(cx,cy,Integer.toString(id_count)));
                id_count++;
            }
        }
        //Clean the dictionary by center points to remove IDS not used anymore
        List<Point> new_center_points=new ArrayList<Point>();
        for(Detector.Recognition obj: object_rect){
            RectF box=obj.getLocation();
            new_center_points.add(new Point(box.centerX(),box.centerY(),obj.getId()));
        }
        //Update dictionary with IDs not used removed
        center_points=new_center_points;
        return boxes;
    }
}
