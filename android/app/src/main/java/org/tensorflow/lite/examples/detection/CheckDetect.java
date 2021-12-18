package org.tensorflow.lite.examples.detection;

public class CheckDetect {
    private static CheckDetect instance = null;
    private boolean checkMaskIncorretly;
    private boolean checkMask;
    private boolean checkNoMask;
    private int contCheckMask;
    private int contCheckNoMask;
    private int contCheckMaskIncorretly;

    private CheckDetect(){
        this.checkMask=true;
        this.checkMaskIncorretly=true;
        this.checkNoMask=true;
        this.contCheckMask=0;
        this.contCheckNoMask=0;
        this.contCheckMaskIncorretly=0;
    }

    public static CheckDetect getInstance() {
        // Crea l'oggetto solo se NON esiste:
        if (instance == null) {
            instance = new CheckDetect();
        }
        return instance;
    }

    synchronized public void setCheckMaskIncorretly(boolean b){
        checkMaskIncorretly=b;
    }
    synchronized public void setCheckMask(boolean b){
        checkMask=b;
    }
    synchronized public void setCheckNoMask(boolean b){
        checkNoMask=b;
    }

    synchronized public boolean getCheckMaskIncorretly(){
        return checkMaskIncorretly;
    }

    synchronized public boolean getCheckMask(){
        return checkMask;
    }

    synchronized public boolean getCheckNoMask(){
        return checkNoMask;
    }

    synchronized public void setContCheckMaskIncorretly(int b){
        contCheckMaskIncorretly=b;
    }
    synchronized public void setContCheckMask(int b){
        contCheckMask=b;
    }
    synchronized public void setContCheckNoMask(int b){
        contCheckNoMask=b;
    }

    synchronized public int getContCheckMaskIncorretly(){
        return contCheckMaskIncorretly;
    }

    synchronized public int getContCheckMask(){
        return contCheckMask;
    }

    synchronized public int getContCheckNoMask(){
        return contCheckNoMask;
    }

}
