package org.tensorflow.lite.examples.detection;

public class Point {
    private float cx;
    private float cy;
    private String id;

    public Point(float cx, float cy, String id){
        this.cx=cx;
        this.cy=cy;
        this.id=id;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

}
