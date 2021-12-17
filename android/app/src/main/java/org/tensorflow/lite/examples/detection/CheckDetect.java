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

    public void setContCheckMaskIncorretly(int b){
        contCheckMaskIncorretly=b;
    }
    public void setContCheckMask(int b){
        contCheckMask=b;
    }
    public void setContCheckNoMask(int b){
        contCheckNoMask=b;
    }

    public int getContCheckMaskIncorretly(){
        return contCheckMaskIncorretly;
    }

    public int getContCheckMask(){
        return contCheckMask;
    }

    public int getContCheckNoMask(){
        return contCheckNoMask;
    }

}
