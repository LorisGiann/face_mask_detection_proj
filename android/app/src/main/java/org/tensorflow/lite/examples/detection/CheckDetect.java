package org.tensorflow.lite.examples.detection;

public class CheckDetect {
    private static CheckDetect instance = null;

    private boolean checkMaskIncorretly;
    private boolean checkMask;
    private boolean checkNoMask;

    private CheckDetect(){
        this.checkMask=true;
        this.checkMaskIncorretly=true;
        this.checkNoMask=true;
    }

    public static CheckDetect getInstance() {
        // Crea l'oggetto solo se NON esiste:
        if (instance == null) {
            instance = new CheckDetect();
        }
        return instance;
    }

    public void setCheckMaskIncorretly(boolean b){
        checkMaskIncorretly=b;
    }
    public void setCheckMask(boolean b){
        checkMask=b;
    }
    public void setCheckNoMask(boolean b){
        checkNoMask=b;
    }

    public boolean getCheckMaskIncorretly(){
        return checkMaskIncorretly;
    }

    public boolean getCheckMask(){
        return checkMask;
    }

    public boolean getCheckNoMask(){
        return checkNoMask;
    }

}
