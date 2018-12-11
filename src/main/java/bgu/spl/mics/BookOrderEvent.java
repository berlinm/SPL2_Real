package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String  orderedBook;
    private int currTick;

    public BookOrderEvent(Customer customer,String orderedBook,int currTick){
        this.customer=customer;
        this.orderedBook=orderedBook;
        this.currTick=currTick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getOrderedBook() {
        return orderedBook;
    }
    public int getCurrTick(){
        return currTick;
    }
}
