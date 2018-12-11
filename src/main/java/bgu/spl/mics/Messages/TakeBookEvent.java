package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event<Boolean> {
    String name;
    public TakeBookEvent(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
}
