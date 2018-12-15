package bgu.spl.mics.application;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {


    public static void main(String[] args) {


        class JsonParser {

            private InitialInventory initialInventory;
            private InitialResources initialResources;
            private Services services;

            public JsonParser(InitialInventory initialInventory, InitialResources initialResources, Services services) {
                this.initialInventory = initialInventory;
                this.initialResources = initialResources;
                this.services = services;
            }

            public JsonParser() {

            }

            public InitialInventory getInitialInventory() {
                return initialInventory;
            }

            public InitialResources getInitialResources() {
                return initialResources;
            }

            public Services getServices() {
                return services;
            }

            class InitialInventory {
                private Book[] bookInventoryInfos;

                public InitialInventory(Book[] bookInventoryInfos) {
                    for (int i = 0; i < bookInventoryInfos.length; i++) {
                        this.bookInventoryInfos[i] = bookInventoryInfos[i];
                    }
                }

                public Book[] getBookInventoryInfos() {
                    return bookInventoryInfos;
                }
            }

            class Book {
                private String bookTitle;
                private int amount;
                private int price;

                public Book(String bookTitle, int amount, int price) {

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
                private Vehicle[][] deliveryVehicles;

                public InitialResources(Vehicle[][] deliveryVehicles) {

                    for (int i = 0; i < deliveryVehicles.length; i++) {
                        for (int j = 0; j < deliveryVehicles[i].length; j++) {
                            this.deliveryVehicles[i][j] = deliveryVehicles[i][j];
                        }
                    }
                }

                public Vehicle[][] getDeliveryVehicles() {
                    return deliveryVehicles;
                }
            }

            class Vehicle {
                private int license;
                private int speed;

                public Vehicle(int license, int speed) {
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

            class Services {

                private Time time;
                private int selling;
                private int inventoryService;
                private int logistic;
                private int resourceService;
                private Customers customers;

                public Services(Time time, int selling, int inventoryService, int logistic, int resourceService, Customers customers) {
                    this.time = time;
                    this.selling = selling;
                    this.inventoryService = inventoryService;
                    this.logistic = logistic;
                    this.resourceService = resourceService;
                    this.customers = customers;
                }

                public Customers getCustomers() {
                    return customers;
                }

                public Time getTime() {
                    return time;
                }

                public int getInventoryService() {
                    return inventoryService;
                }

                public int getLogistic() {
                    return logistic;
                }

                public int getResourceService() {
                    return resourceService;
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

            class Customers {

                Customer[] customers;

                public Customers(Customer[] customers) {
                    this.customers = customers;
                }

                public Customer[] getCustomers() {
                    return customers;
                }
            }

            class Customer {

                private int id;
                private String name;
                private String address;
                private int distance;
                private CreditCard creditCard;
                private OrderSchedule orderSchedule;

                public Customer(int id, String name, String address, int distance, CreditCard creditCard, OrderSchedule orderSchedule) {
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

                public OrderSchedule getOrderSchedule() {
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

            class OrderSchedule {
                private BookByTick[] bookByTicks;

                public OrderSchedule(BookByTick[] bookByTicks){
                    this.bookByTicks=bookByTicks;
                }

                public BookByTick[] getBookByTicks() {
                    return bookByTicks;
                }
            }


            class BookByTick {
                 private String bookTitle;
                 private int tick;

                 public BookByTick(String bookTitle, int tick) {
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

        JsonParser jsonParser=new JsonParser();

        try(Reader reader=new FileReader(args[0])) {
            Gson gson=new GsonBuilder().create();
            jsonParser=gson.fromJson(reader,JsonParser.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Inventory inventory=Inventory.getInstance();
        JsonParser.Book[] books=jsonParser.getInitialInventory().getBookInventoryInfos();
        BookInventoryInfo[] ToLoad=new BookInventoryInfo[books.length];
        for(int i=0;i<books.length;i++){
            BookInventoryInfo newbook=new BookInventoryInfo(books[i].getBookTitle(),books[i].getAmount(),books[i].getPrice());
            ToLoad[i]=newbook;
        }
        inventory.load(ToLoad);

        ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
        JsonParser.Vehicle[][] vehicles=jsonParser.getInitialResources().getDeliveryVehicles();
        for(int i=0;i<vehicles.length;i++){
            for(int j=0;j<vehicles[i].length;j++){
                DeliveryVehicle deliveryVehicle=new DeliveryVehicle(vehicles[i][j].getLicense(),vehicles[i][j].getSpeed());
                resourcesHolder.releaseVehicle(deliveryVehicle);
            }
        }










    }
}
