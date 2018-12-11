package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String  orderedBook;
    private int orderTick;

    public BookOrderEvent(Customer customer,String orderedBook,int orderTick){
        this.customer=customer;
        this.orderedBook=orderedBook;
        this.orderTick=orderTick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getOrderedBook() {
        return orderedBook;
    }
    public int getOrderTick(){
        return orderTick;
    }
}
