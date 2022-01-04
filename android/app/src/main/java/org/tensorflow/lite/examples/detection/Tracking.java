package org.tensorflow.lite.examples.detection;

import android.graphics.RectF;

import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.util.ArrayList;
import java.util.List;

public class Tracking {
    private final int DISTANCE_SAME_OBJECT = 25;
    private  List<Point> center_points;
    private int id_count = 0;

    public Tracking(){
        this.center_points= new ArrayList<Point>();
        this.id_count = 1;
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
        List<Detector.Recognition> boxes= new ArrayList<>();
        // Get center point of new object
        double dist=0;
        for(Detector.Recognition obj: object_rect){
            RectF rect=obj.getLocation();
            float cx=rect.centerX();
            float cy=rect.centerY();
            //System.out.println("CX: "+cx+" CY: "+cy+" center_points.size(): "+center_points.size());
            //Find out if that object was detected already
            boolean same_object_detected=false;
            for (int i=0;i<center_points.size();i++){
                 dist=calculateDistanceBetweenPointsWithHypot(cx,cy,
                        center_points.get(i).getCx(),center_points.get(i).getCy());
                //System.out.println("DIS: "+dist);
                if (dist<DISTANCE_SAME_OBJECT){
                    center_points.get(i).setCx(cx);
                    center_points.get(i).setCy(cy);

                    boxes.add(new Detector.Recognition(center_points.get(i).getId(),obj.getTitle(),obj.getConfidence(), obj.getLocation()));
                    same_object_detected=true;
                    break;
                }
            }
            //New object is detected
            if (same_object_detected==false){
                //System.out.println("NUOVO OGGETTO");
                center_points.add(new Point(cx,cy,Integer.toString(id_count)));
                boxes.add(new Detector.Recognition(center_points.get(center_points.size()-1).getId(),
                        obj.getTitle(),obj.getConfidence(), obj.getLocation()));
                id_count++;
            }
        }
        //Clean the dictionary by center points to remove IDS not used anymore
        List<Point> new_center_points=new ArrayList<Point>();
        for(Detector.Recognition obj: object_rect){
            //System.out.println("Add new center points");
            RectF box=obj.getLocation();
            new_center_points.add(new Point(box.centerX(),box.centerY(),obj.getId()));
        }
        //Update dictionary with IDs not used removed
        //System.out.println("SIZE CENTER_POINTS: "+center_points.size());
        center_points=new_center_points;
        //System.out.println("SIZE NEW_CENTER_POINTS: "+new_center_points.size());
        return boxes;
    }
}
