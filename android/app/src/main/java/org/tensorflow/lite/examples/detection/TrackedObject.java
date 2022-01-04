package org.tensorflow.lite.examples.detection;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

public class TrackedObject {
    private String id;
    private Timestamp timestamp;
    private ArrayList<Point> trackedRecord;
    private int lastUpdateFrameNum;

    //COSTRUCTOR
    public TrackedObject(String id) {
        this.id = id;
        this.updateTimestamp();
        this.lastUpdateFrameNum =0;
        this.trackedRecord = new ArrayList<>();
    }

    //PRIVATE UTILITY FUNCTION
    private void updateTimestamp(){
        long time = System.currentTimeMillis();
        this.timestamp = new Timestamp(time);
    }

    //PUBLIC FUNCTION
    public String getId() { return id; }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public int getLastUpdateFrameNum(){
        return this.lastUpdateFrameNum;
    }
    public ArrayList<Point> getPointHistory() {
        return trackedRecord;
    }

    public void setNewPoint(Point trackedPoint, int frameNum) {
        this.lastUpdateFrameNum = frameNum;
        this.trackedRecord.add(trackedPoint);
        this.updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedObject that = (TrackedObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        Point p = getPointHistory().get(getPointHistory().size() - 1);
        return "(" + p.getCx() + "," + p.getCy() + ")";
    }
}
