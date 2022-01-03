package org.tensorflow.lite.examples.detection;

import java.sql.Timestamp;
import java.util.ArrayList;

public class TrackedObject {
    private String id;
    private Timestamp timestamp;
    private ArrayList<Point> trackedRecord;
    private int cont;

    //COSTRUCTOR
    public TrackedObject(String id) {
        this.id = id;
        this.updateTimestamp();
        this.cont=0;
        this.trackedRecord = new ArrayList<>();
    }

    //PRIVATE UTILITY FUNCTION
    private void updateTimestamp(){
        long time = System.currentTimeMillis();
        this.timestamp = new Timestamp(time);
    }

    //PUBLIC FUNCTION
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public ArrayList<Point> getTrackedRecord() {
        return trackedRecord;
    }

    public void setTrackedRecord(Point trackedPoint) {
        this.trackedRecord.add(trackedPoint);
        this.updateTimestamp();
        this.cont++;
    }

    public int getCont(){
        return this.cont;
    }

}
