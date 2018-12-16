package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event<Boolean> {
    String bookName;
    String senderName;
    public TakeBookEvent(String bookName, String senderName){
        this.bookName =bookName;
        this.senderName = senderName;
    }
    public String getBookName(){
        return this.bookName;
    }

    public String getSenderName() {
        return senderName;
    }
}
