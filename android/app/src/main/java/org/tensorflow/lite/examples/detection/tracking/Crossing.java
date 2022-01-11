package org.tensorflow.lite.examples.detection.tracking;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.tensorflow.lite.examples.detection.CheckDetect;
import org.tensorflow.lite.examples.detection.env.Logger;

import java.util.List;

//this class takes as an input a list of TrackedObjects, and keeps a counter of the objects which passed a certain line
public class Crossing {
    private static Crossing instance=null;
    private final Logger logger = new Logger();
    private Matrix frameToCanvasMatrix;
    private int countMask;
    private int countNoMask;
    public float X_COLUMN_CONT_PASS = 0.5f;
    private int canvasWidth=-1, canvasHeight=-1;

    //SINGLETON
    private Crossing(){
        this.countMask = 0;
        this.countNoMask = 0;
    }

    public static Crossing getInstance(){
        if (instance==null){
            instance=new Crossing();
        }
        return instance;
    }

    synchronized public int getCountMask(){
        return countMask;
    }

    synchronized public int getCountNoMask(){
        return countNoMask;
    }

    synchronized public void update(List<TrackedObject> trackedObjects, int currentFrame) {
        if (canvasWidth<0 || canvasHeight<0 || frameToCanvasMatrix== null) return; //ignore the values until we have all the data we need
        float xCoord = X_COLUMN_CONT_PASS*canvasWidth;
        for(TrackedObject to: trackedObjects){
            //get last two point from history trackedObject
            if (to.getPointHistory().size()>1 && to.getLastUpdateFrameNum()==currentFrame){
                Point p1 = convertPointForCanvas(to.getPointHistory().get(to.getPointHistory().size()-2));
                Point p2 = convertPointForCanvas(to.getPointHistory().get(to.getPointHistory().size()-1));
                logger.w("LABEL: " + to.getLabel());
                if(p1.getCx()<xCoord && p2.getCx()>=xCoord){
                    if (to.getLabel().equalsIgnoreCase("mask_ok")){
                        this.countMask++;
                    }else{
                        this.countNoMask++;
                    }

                }
                if(p1.getCx()>=xCoord && p2.getCx()<xCoord){
                    if (to.getLabel().equalsIgnoreCase("mask_ok")){
                        this.countMask--;
                    }else{
                        this.countNoMask--;
                    }
                }
            }
        }

        logger.w("CONT PASS: " + this.countMask +" | "+this.countNoMask);
    }

    public void drawCrossingLine(Canvas canvas, int color) {
        if (canvasWidth<0 || canvasHeight<0 || frameToCanvasMatrix== null) return; //ignore the values until we have all the data we need
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(5);
        linePaint.setColor(color);
        float f0[] = {X_COLUMN_CONT_PASS*canvasWidth, 0, X_COLUMN_CONT_PASS*canvasWidth, canvasHeight};
        //frameToCanvasMatrix.mapPoints(f0);
        canvas.drawLines(f0, linePaint);
    }

    public void setCanvasSize(int width, int height) {
        this.canvasHeight=height;
        this.canvasWidth=width;
    }

    public void setConversionMatrix(Matrix frameToCanvasMatrix) {
        this.frameToCanvasMatrix = frameToCanvasMatrix;
    }

    private Point convertPointForCanvas(Point p){
        float f0[] = {p.getCx(), p.getCy()}; //startx, starty, stopx, stopy
        frameToCanvasMatrix.mapPoints(f0);
        return new Point(f0[0], f0[1]);
    }

    synchronized public void resetCount(){
        this.countMask=0;
        this.countNoMask=0;
    }
}
