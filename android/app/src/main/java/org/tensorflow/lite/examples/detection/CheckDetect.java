package org.tensorflow.lite.examples.detection;

public class CheckDetect {
    private static CheckDetect instance = null;
    private boolean checkMaskIncorretly;
    private boolean checkMask;
    private boolean checkNoMask;
    private boolean checkModeTracking;
    private boolean checkLineTracking;
    private boolean checkObjectTracking;
    private int contCheckMask;
    private int contCheckNoMask;

    private CheckDetect(){
        this.checkMask=true;
        this.checkMaskIncorretly=true;
        this.checkNoMask=true;
        this.contCheckMask=0;
        this.contCheckNoMask=0;
        this.checkModeTracking=false;
        this.checkLineTracking=false;
        this.checkObjectTracking=false;
    }

    public static CheckDetect getInstance() {
        // Crea l'oggetto solo se NON esiste:
        if (instance == null) {
            instance = new CheckDetect();
        }
        return instance;
    }

    synchronized public void setCheckMask(boolean b){
        checkMask=b;
    }
    synchronized public void setCheckNoMask(boolean b){
        checkNoMask=b;
    }
    synchronized public void setCheckModeTracking(boolean b) { checkModeTracking=b; }
    synchronized public void setCheckLineTracking(boolean b) { checkLineTracking=b; }
    synchronized public void setCheckObjectTracking(boolean b) { checkObjectTracking=b; }

    synchronized  public boolean getCheckModeTracking() { return checkModeTracking; };
    synchronized public boolean getCheckLineTracking() { return checkLineTracking; }
    synchronized public boolean getCheckObjectTracking() { return checkObjectTracking; }

    synchronized public boolean getCheckMask(){
        return checkMask;
    }

    synchronized public boolean getCheckNoMask(){
        return checkNoMask;
    }

    synchronized public void setContCheckMask(int b){
        contCheckMask=b;
    }
    synchronized public void setContCheckNoMask(int b){
        contCheckNoMask=b;
    }

    synchronized public int getContCheckMask(){
        return contCheckMask;
    }

    synchronized public int getContCheckNoMask(){
        return contCheckNoMask;
    }



}
