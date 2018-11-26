package bgu.spl.mics.application.passiveObjects;

import com.sun.corba.se.impl.io.TypeMismatchException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory testInventory;
    @Before
    public void setUp() throws Exception {
        this.testInventory = Inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        if (!(this.testInventory instanceof Inventory))
            throw new TypeMismatchException();
    }

    @Test
    public void load() {
        BookInventoryInfo[] bookInventoryInfos = new BookInventoryInfo[0];
        this.testInventory.load(bookInventoryInfos);
        if (!this.testInventory.isEmpty())
            throw new ExceptionInInitializerError();
        boolean nullTested = false;
        bookInventoryInfos = null;
        try {
            this.testInventory.load(bookInventoryInfos);
        }
        catch (Exception e){
            nullTested = true;
        }
        if (!nullTested) throw new ExceptionInInitializerError();
    }

    @Test
    public void take() throws Exception {
        int prev = this.testInventory.getTotalNumberOfBooks();
        if (testInventory.take("Harry Potter").equals(OrderResult.valueOf("NOT_IN_STOCK")) && prev != this.testInventory.getTotalNumberOfBooks())
            throw new Exception("If a book is not in stock, the number of books should remain the same");
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
    }

    @Test
    public void printInventoryToFile() {
    }
}