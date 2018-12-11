package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.awt.print.Book;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String  orderedBook;

    public BookOrderEvent(Customer customer,String orderedBook){
        this.customer=customer;
        this.orderedBook=orderedBook;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getOrderedBook() {
        return orderedBook;
    }
}
