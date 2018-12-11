package bgu.spl.mics.Messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int currentTick;
    public TickBroadcast(int currentTick){
        this.currentTick = currentTick;
    }
    public int getCurrentTick(){
        return this.currentTick;
    }
}
