package bgu.spl.mics;

public class TakeBookEvent implements Event<Boolean> {
    String name;

    public TakeBookEvent(String name){
        this.name=name;
    }

    public String getName(){
        return this.name;
    }
}
