package bgu.spl.mics;

import com.sun.org.apache.xml.internal.security.signature.ReferenceNotInitializedException;

import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends MicroService {

    private static class SingletonHolder {
        private static TimeService timerInstance = null;
    }
    private Timer m_Timer;
    private int currentTick, duration, speed;
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    private TimeService(String name, int speed, int duration) {
        super(name);
        this.currentTick = 0;
        this.duration = duration;
        this.speed = speed;
    }
    //TODO: comment inside
    @Override
    protected void initialize() {
        this.m_Timer = new Timer();
        this.m_Timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentTick < duration)
                {
                    MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast(currentTick));
                    currentTick++;
                }
                else {
                    //means the timer is up longer than the set duration (need to be terminated?)
                }
            }
        }, 0, this.speed);
    }

    public static boolean createInstance(String name, int speed, int duration) {
        if (SingletonHolder.timerInstance == null){
            SingletonHolder.timerInstance = new TimeService(name, speed, duration);
            return true;
        }
        return false;
    }
    public static TimeService getInstance() throws ReferenceNotInitializedException {
        if (SingletonHolder.timerInstance != null)
            return SingletonHolder.timerInstance;
        else
            throw new ReferenceNotInitializedException();
    }
    public int getCurrentTick(){
        return currentTick;
    }
}