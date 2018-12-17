package bgu.spl.mics;

public class MySemaphore {
    private int acquiring, releasing;
    public MySemaphore(){
        acquiring = 0;
        releasing = 0;
    }
    public synchronized void acquireAn_Acquire(){
        while (releasing > 0){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        acquiring++;
    }
    public synchronized void acquire_A_Release(){
        while (acquiring > 0){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        releasing++;
    }
    public synchronized void releaseAn_Acquire(){
        acquiring--;
        this.notifyAll();
    }
    public synchronized void releaseA_Release(){
        releasing--;
        this.notifyAll();
    }
}
