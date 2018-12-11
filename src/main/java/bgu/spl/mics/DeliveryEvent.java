package bgu.spl.mics;


import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event  {

    private Customer customer;


    public DeliveryEvent(Customer customer){
        this.customer=customer;
    }

    public Customer getCustomer(){
        return this.customer;
    }



}
