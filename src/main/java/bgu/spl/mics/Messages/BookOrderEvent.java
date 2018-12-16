package bgu.spl.mics.Messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String  orderedBook;
    private int orderTick;
    private String senderName;
    public BookOrderEvent(Customer customer,String orderedBook,int orderTick, String senderName){
        this.customer=customer;
        this.orderedBook=orderedBook;
        this.orderTick=orderTick;
        this.senderName = senderName;
    }

    public Customer getCustomer() {
        return customer;
    }
    public String getSenderName(){return this.senderName;}
    public String getOrderedBook() {
        return orderedBook;
    }
    public int getOrderTick(){
        return orderTick;
    }
}
