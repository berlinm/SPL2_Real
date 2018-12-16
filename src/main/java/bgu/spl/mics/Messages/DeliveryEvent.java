package bgu.spl.mics.Messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event<Boolean> {
    private String senderName;
    private Customer customer;
    public DeliveryEvent(Customer customer, String senderName){
        this.customer=customer;
        this.senderName = senderName;
    }
    public Customer getCustomer(){
        return this.customer;
    }

    public String getSenderName() {
        return senderName;
    }
}
