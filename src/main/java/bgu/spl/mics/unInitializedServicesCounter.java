package bgu.spl.mics;

public class unInitializedServicesCounter {
    private static unInitializedServicesCounter instance = new unInitializedServicesCounter();
    private unInitializedServicesCounter(){
        this.unInitialized = 0;
    }
    private int unInitialized;
    public static unInitializedServicesCounter getInstance() {
        return instance;
    }
    public boolean AllInitialized(){
        return this.unInitialized == 0;
    }
    public void newService(){
        this.unInitialized++;
    }
    public void initializationEnded(){
        this.unInitialized--;
    }
}
