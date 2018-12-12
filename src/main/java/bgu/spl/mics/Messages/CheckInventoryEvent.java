package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckInventoryEvent implements Event<AtomicInteger> {

    private String name;

    public CheckInventoryEvent(String name){
        this.name=name;
    }

    public String getName(){
        return this.name;
    }
}
