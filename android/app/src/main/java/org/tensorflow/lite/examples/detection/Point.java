package org.tensorflow.lite.examples.detection;

public class Point {
    private float cx;
    private float cy;

    public Point(float cx, float cy) {
        this.cx = cx;
        this.cy = cy;
    }

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

}
