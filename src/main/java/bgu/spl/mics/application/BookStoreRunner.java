package bgu.spl.mics.application;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Messages.BookOrderEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xml.internal.security.Init;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    class JsonParser {

        private InitialInventory [] initialInventory;
        private InitialResources [] initialResources;
        private services services;

        public JsonParser(InitialInventory [] initialInventory, InitialResources[]  initialResources, services services) {
            this.initialInventory = initialInventory;
            this.initialResources = initialResources;
            this.services = services;
        }

        public InitialInventory [] getInitialInventory() {
            return initialInventory;
        }

        public InitialResources [] getInitialResources() {
            return initialResources;
        }

        public services getServices() {
            return services;
        }

        class InitialInventory {
            private String bookTitle;
            private int amount;
            private int price;

            public InitialInventory(String bookTitle, int amount, int price) {

                this.amount = amount;
                this.price = price;
                this.bookTitle = bookTitle;

            }

            public int getAmount() {
                return amount;
            }

            public int getPrice() {
                return price;
            }

            public String getBookTitle() {
                return bookTitle;
            }
        }

        class InitialResources {
            private vehicles [] vehicles;

            public InitialResources(vehicles[] vehicles) {

                for (int i = 0; i < vehicles.length; i++) {
                        this.vehicles[i] = vehicles[i];

                }
            }

            public vehicles[] getDeliveryVehicles() {
                return vehicles;
            }
        }

        class vehicles {
            private int license;
            private int speed;

            public vehicles(int license, int speed) {
                this.license = license;
                this.speed = speed;
            }

            public int getLicense() {
                return license;
            }

            public int getSpeed() {
                return speed;
            }
        }

        class services {

            private Time time;
            private int selling;
            private int inventoryService;
            private int logistics;
            private int resourcesService;
            private customers [] customers;

            public services(Time time, int selling, int inventoryService, int logistics, int resourcesService, customers [] customers) {
                this.time = time;
                this.selling = selling;
                this.inventoryService = inventoryService;
                this.logistics = logistics;
                this.resourcesService = resourcesService;
                this.customers = customers;
            }

            public customers [] getCustomers() {
                return customers;
            }

            public Time getTime() {
                return time;
            }

            public int getInventoryService() {
                return inventoryService;
            }

            public int getLogistic() {
                return logistics;
            }

            public int getResourceService() {
                return resourcesService;
            }

            public int getSelling() {
                return selling;
            }
        }

        class Time {
            private int speed;
            private int duration;

            public Time(int speed, int duration) {
                this.speed = speed;
                this.duration = duration;
            }

            public int getSpeed() {
                return speed;
            }

            public int getDuration() {
                return duration;
            }
        }

        class customers {

            private int id;
            private String name;
            private String address;
            private int distance;
            private CreditCard creditCard;
            private orderSchedule [] orderSchedule;

            public customers(int id, String name, String address, int distance, CreditCard creditCard, orderSchedule[] orderSchedule) {
                this.id = id;
                this.name = name;
                this.address = address;
                this.distance = distance;
                this.creditCard = creditCard;
                this.orderSchedule = orderSchedule;
            }

            public CreditCard getCreditCard() {
                return creditCard;
            }

            public int getDistance() {
                return distance;
            }

            public int getId() {
                return id;
            }

            public String getAddress() {
                return address;
            }

            public String getName() {
                return name;
            }

            public orderSchedule[] getOrderSchedule() {
                return orderSchedule;
            }
        }

        class CreditCard {
            private int number;
            private int amount;

            public CreditCard(int number, int amount) {
                this.amount = amount;
                this.number = number;
            }

            public int getAmount() {
                return amount;
            }

            public int getNumber() {
                return number;
            }
        }

        class orderSchedule {
            private String bookTitle;
            private int tick;

            public orderSchedule(String bookTitle, int tick) {
                this.bookTitle = bookTitle;
                this.tick = tick;
            }

            public String getBookTitle() {
                return bookTitle;
            }

            public int getTick() {
                return tick;
            }
        }

    }


    public static void main(String[] args) {
        ConcurrentHashMap<Integer, Customer> Customers = new ConcurrentHashMap<Integer, Customer>();
        JsonParser jsonParser=null;

        try(Reader reader=new FileReader(args[0])) {
            Gson gson=new GsonBuilder().create();
            jsonParser = gson.fromJson(reader,JsonParser.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Inventory inventory=Inventory.getInstance();
        JsonParser.InitialInventory[] books=jsonParser.getInitialInventory();
        BookInventoryInfo[] ToLoad=new BookInventoryInfo[books.length];
        for(int i=0;i<books.length;i++){
            BookInventoryInfo newbook=new BookInventoryInfo(books[i].getBookTitle(),books[i].getAmount(),books[i].getPrice());
            ToLoad[i]=newbook;
        }
        inventory.load(ToLoad);

        ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
        for(int j=0;j<jsonParser.initialResources.length;j++) {
            JsonParser.vehicles[] vehicles = jsonParser.getInitialResources()[j].getDeliveryVehicles();
            DeliveryVehicle[] vehicles11=new DeliveryVehicle[vehicles.length];
            for (int i = 0; i < vehicles.length; i++) {
                DeliveryVehicle deliveryVehicle = new DeliveryVehicle(vehicles[i].getLicense(), vehicles[i].getSpeed());
                vehicles11[i]=deliveryVehicle;
            }
            resourcesHolder.load(vehicles11);
        }

        JsonParser.services services=jsonParser.getServices();

        TimeService timeService=new TimeService(services.getTime().speed,services.getTime().duration);

        LinkedList<MicroService> MicroServices=new LinkedList<>();

        for(int i=0;i<services.selling;i++){
            MicroServices.add(new SellingService("Selling Service "+i));
        }
        for(int i=0;i<services.inventoryService;i++){
            MicroServices.add(new InventoryService("Inventory Service "+i));
        }
        for(int i=0;i<services.logistics;i++){
            MicroServices.add(new LogisticsService("Logistic Service" +i));
        }
        for(int i=0;i<services.resourcesService;i++){
            MicroServices.add(new ResourceService("Resource Service "+i));
        }

        JsonParser.customers [] initCustomers=jsonParser.getServices().getCustomers();

        for(int i=0;i<initCustomers.length;i++){

            JsonParser.customers customer=initCustomers[i];
            Customer cs = new Customer(customer.getId(),customer.getName(),customer.getAddress(),customer.getDistance(),new LinkedList<OrderReceipt>(),customer.getCreditCard().getNumber(),customer.getCreditCard().getAmount());
            Customers.put(cs.getId(), cs);
            ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>> myhash=new ConcurrentHashMap<AtomicInteger, BlockingQueue<BookOrderEvent>>();

            JsonParser.orderSchedule[] orderSchedule=customer.getOrderSchedule();
            for(int j=0;j<orderSchedule.length;j++){
                AtomicInteger integer=new AtomicInteger(orderSchedule[j].getTick());
                if(myhash.containsKey(integer)){
                    myhash.get(orderSchedule[j].getTick()).add(new BookOrderEvent(cs,orderSchedule[j].getBookTitle(),orderSchedule[j].getTick()));
                }else{
                    myhash.put(integer,new LinkedBlockingQueue<BookOrderEvent>());
                    myhash.get(integer).add(new BookOrderEvent(cs,orderSchedule[j].getBookTitle(),orderSchedule[j].getTick()));
                }
            }

            MicroServices.add(new APIService("API Service "+i, myhash,cs));
        }
        //Output 1: Customers
        try {
            // Saving Customers in a file
            FileOutputStream outCustomers = new FileOutputStream(args[1]);
            ObjectOutputStream out = new ObjectOutputStream(outCustomers);
            // Method for serialization of object
            out.writeObject(Customers);
            out.close();
            outCustomers.close();
        }
        catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        Vector<Thread> threads=new Vector<Thread>();

        threads.add(new Thread(timeService));

        for(int j=0;j<MicroServices.size();j++){
            threads.add(new Thread(MicroServices.get(j)));
        }



        for(int x=0;x<threads.size();x++) {
            threads.get(x).start();
        }

        for (int i=0;i<threads.size();i++) {
            try{ threads.get(i).join();}
            catch (Exception e) {e.printStackTrace();}
        }
        //Output 2: Books
        Inventory.getInstance().printInventoryToFile(args[2]);
        //Output 3: Receipts
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        //Output 4:
        try {
            // Saving Customers in a file
            FileOutputStream outMoneyRegister = new FileOutputStream
                    (args[4]);
            ObjectOutputStream out1 = new ObjectOutputStream
                    (outMoneyRegister);
            // Method for serialization of object
            out1.writeObject(MoneyRegister.getInstance());
            out1.close();
            outMoneyRegister.close();
        }
        catch (IOException ex) {
            System.out.println("IOException is caught");
        }
    }
}
