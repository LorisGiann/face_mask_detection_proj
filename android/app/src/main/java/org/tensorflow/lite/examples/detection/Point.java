package org.tensorflow.lite.examples.detection;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Float.compare(point.cx, cx) == 0 && Float.compare(point.cy, cy) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cx, cy);
    }

    @Override
    public String toString() {
        return "Point{" +
                "cx=" + cx +
                ", cy=" + cy +
                '}';
    }
}
