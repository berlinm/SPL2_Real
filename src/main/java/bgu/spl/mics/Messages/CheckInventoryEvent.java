package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckInventoryEvent implements Event<AtomicInteger> {

    private String getBookName;
    private String sender;
    public CheckInventoryEvent(String bookName, String sender){
        this.getBookName =bookName;
        this.sender = sender;
    }
    public String getSender(){
        return this.sender;
    }
    public String getBookName(){
        return this.getBookName;
    }
}
