package org.tensorflow.lite.examples.detection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tensorflow.lite.examples.detection.tflite.Detector;

public class TrackingTest {

    Tracking tracker;

    @Before
    public void init() {
        tracker = new Tracking();
    }

    @Test
    public void update_initial_objects() {
        List<Detector.Recognition> input_objects = new ArrayList<Detector.Recognition>();
        input_objects.add(new Detector.Recognition("a","a", 0.96f, new RectF(100f,50f,100f,50f)));
        input_objects.add(new Detector.Recognition("b","b", 1f, new RectF(100f,150f,100f,150f)));
        input_objects.add(new Detector.Recognition("c","c", 0.99f, new RectF(200f,80f,200f,80f)));
        input_objects.add(new Detector.Recognition("d","d", 0.49f, new RectF(10f,40f,10f,40f))); //too low probability
        tracker.update(input_objects);
        List<TrackedObject> trackedObjects = tracker.getTrackedObjects();
        assertEquals(3, trackedObjects.size());
        assertTrue(getByStartPosition(trackedObjects,100f,50f)!=null);
        assertTrue(getByStartPosition(trackedObjects,100f,150f)!=null);
        assertTrue(getByStartPosition(trackedObjects,200f,80f)!=null);
        assertFalse(trackedObjects.get(0).getId().equals(trackedObjects.get(1).getId()));
        assertFalse(trackedObjects.get(1).getId().equals(trackedObjects.get(2).getId()));
        assertFalse(trackedObjects.get(2).getId().equals(trackedObjects.get(0).getId()));
    }

    @Test
    public void update_choose_who_assign_new_point() {
        List<Detector.Recognition> input_objects = new ArrayList<Detector.Recognition>();
        input_objects.add(new Detector.Recognition("a","a", 0.96f, new RectF(10f,10f,10f,10f)));
        input_objects.add(new Detector.Recognition("b","b", 1f, new RectF(10f,20f,10f,20f)));
        tracker.update(input_objects);
        input_objects.clear();
        input_objects.add(new Detector.Recognition("c","c", 1f, new RectF(20f,10f,20f,10f)));
        tracker.update(input_objects);
        //a and c should now be joined together
        List<TrackedObject> trackedObjects = tracker.getTrackedObjects();
        assertEquals(2, trackedObjects.size());
        assertEquals(2,getByStartPosition(trackedObjects,10f,10f).getPointHistory().size());
        assertEquals(1,getByStartPosition(trackedObjects,10f,20f).getPointHistory().size());
        assertFalse(trackedObjects.get(0).getId().equals(trackedObjects.get(1).getId()));
    }

    @Test
    public void update_2_steps_assignment() {
        List<Detector.Recognition> input_objects = new ArrayList<Detector.Recognition>();
        input_objects.add(new Detector.Recognition("a","a", 0.96f, new RectF(0f,20f,0f,20f)));
        input_objects.add(new Detector.Recognition("b","b", 1f, new RectF(20f,30f,20f,30f)));
        tracker.update(input_objects);
        input_objects.clear();
        input_objects.add(new Detector.Recognition("c","c", 1f, new RectF(20f,10f,20f,10f)));
        input_objects.add(new Detector.Recognition("d","d", 1f, new RectF(20f,20f,20f,20f)));
        tracker.update(input_objects);
        //associations: b->d, a->c
        List<TrackedObject> trackedObjects = tracker.getTrackedObjects();
        assertEquals(2, trackedObjects.size());
        List<Point> h = getByStartPosition(trackedObjects,0f,20f).getPointHistory(); //a
        assertEquals(2,h.size());
        assertEquals(new Point(0f,20f),h.get(0));
        assertEquals(new Point(20f,10f),h.get(1));
        h = getByStartPosition(trackedObjects,20f,30f).getPointHistory(); //b
        assertEquals(2,h.size());
        assertEquals(new Point(20f,30f),h.get(0));
        assertEquals(new Point(20f,20f),h.get(1));
        assertFalse(trackedObjects.get(0).getId().equals(trackedObjects.get(1).getId()));
    }

    @Test
    public void temporaneous_missing_object() {
        List<Point> h;
        List<TrackedObject> trackedObjects;
        List<Detector.Recognition> input_objects = new ArrayList<Detector.Recognition>();
        input_objects.add(new Detector.Recognition("a","a", 0.96f, new RectF(0f,0f,0f,0f)));
        input_objects.add(new Detector.Recognition("b","b", 1f, new RectF(0f,10f,0f,10f)));
        input_objects.add(new Detector.Recognition("c","c", 1f, new RectF(0f,30f,0f,30f)));
        tracker.update(input_objects);
        trackedObjects = tracker.getTrackedObjects();
        assertEquals(3, trackedObjects.size());
        assertEquals(0,getByStartPosition(trackedObjects,0f,0f).getLastUpdateFrameNum());
        assertEquals(0,getByStartPosition(trackedObjects,0f,10f).getLastUpdateFrameNum());
        assertEquals(0,getByStartPosition(trackedObjects,0f,30f).getLastUpdateFrameNum());

        input_objects.clear();
        input_objects.add(new Detector.Recognition("a","a", 0.91f, new RectF(10f,0f,10f,0f)));
        input_objects.add(new Detector.Recognition("b","b", 0.1f, new RectF(10f,10f,10f,10f))); //simulate a missed object (too low probability)
        input_objects.add(new Detector.Recognition("c","c", 0.1f, new RectF(10f,30f,10f,30f))); //simulate a missed object (too low probability)
        tracker.update(input_objects);
        trackedObjects = tracker.getTrackedObjects();
        assertEquals(3, trackedObjects.size());
        assertEquals(1,getByStartPosition(trackedObjects,0f,0f).getLastUpdateFrameNum());
        assertEquals(0,getByStartPosition(trackedObjects,0f,10f).getLastUpdateFrameNum());
        assertEquals(0,getByStartPosition(trackedObjects,0f,30f).getLastUpdateFrameNum());

        input_objects.clear();
        input_objects.add(new Detector.Recognition("a","a", 0.91f, new RectF(20f,0f,20f,0f)));
        input_objects.add(new Detector.Recognition("b","b", 0.91f, new RectF(30f,10f,30f,10f)));
        input_objects.add(new Detector.Recognition("c","c", 0.91f, new RectF(20f,40f,20f,40f)));
        tracker.update(input_objects);
        trackedObjects = tracker.getTrackedObjects();
        assertEquals(3, trackedObjects.size());
        assertEquals(2,getByStartPosition(trackedObjects,0f,0f).getLastUpdateFrameNum());
        assertEquals(2,getByStartPosition(trackedObjects,0f,10f).getLastUpdateFrameNum());
        assertEquals(2,getByStartPosition(trackedObjects,0f,30f).getLastUpdateFrameNum());

        h = getByStartPosition(trackedObjects,0f,0f).getPointHistory(); //a
        assertEquals(3,h.size());
        assertEquals(new Point(0f,0f),h.get(0));
        assertEquals(new Point(10f,0f),h.get(1));
        assertEquals(new Point(20f,0f),h.get(2));
        h = getByStartPosition(trackedObjects,0f,10f).getPointHistory(); //b
        assertEquals(2,h.size());
        assertEquals(new Point(0f,10f),h.get(0));
        assertEquals(new Point(30f,10f),h.get(1));
        h = getByStartPosition(trackedObjects,0f,30f).getPointHistory(); //c
        assertEquals(2,h.size());
        assertEquals(new Point(0f,30f),h.get(0));
        assertEquals(new Point(20f,40f),h.get(1));
    }

    private TrackedObject getByStartPosition(List<TrackedObject> trackedObjects, float x, float y) {
        for(TrackedObject trackedObject: trackedObjects){
            Point p = trackedObject.getPointHistory().get(0);
            if (p.getCx()==x && p.getCy()==y) return trackedObject;
        }
        return null;
    }


   /*
    @Test
    public void guavaTablesEmptyTest() {
        Table<String, Integer, Double> table = HashBasedTable.create();
        table.put("a", 1, 1.0);
        table.put("a", 2, 2.0);
        table.put("a", 3, 3.0);
        table.put("b", 1, 4.0);
        table.put("b", 2, 5.0);
        table.put("b", 3, 6.0);
        table.put("c", 1, 7.0);
        table.put("c", 2, 8.0);
        table.put("c", 3, 9.0);
        //table.remove("b",2);
        table.row("b").clear();
        table.column(2).clear();
        assertEquals(4,table.size());
        System.out.println(table.toString());

        Table<String, String, String> myTable = HashBasedTable.create();
        myTable.put("a", "1", " ");
        myTable.put("a", "2", " ");
        myTable.put("a", "3", " ");
        myTable.put("a", "4", " ");
        myTable.row("a").clear();
        assertTrue(myTable.isEmpty());
    }
*/
}