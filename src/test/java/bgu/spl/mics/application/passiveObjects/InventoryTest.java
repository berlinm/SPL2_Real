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
        assertNotNull(Inventory.getInstance());
    }

    @Test
    public void load_AvailibiltyAndGetPrice_Test() {
        BookInventoryInfo[] bookInventoryInfos = new BookInventoryInfo[1];
        bookInventoryInfos[0] = new BookInventoryInfo("Harry Potter", 8, 80);
        testInventory.load(bookInventoryInfos);
        assertEquals(8, testInventory.checkAvailabiltyAndGetPrice("Harry Potter"));
    }

    @Test
    public void take(){
        assertTrue(testInventory.take("Harry Poker").equals(OrderResult.NOT_IN_STOCK));
        BookInventoryInfo[] bookInventoryInfos = new BookInventoryInfo[1];
        bookInventoryInfos[0] = new BookInventoryInfo("Harry Potter", 8, 80);
        testInventory.load(bookInventoryInfos);
        assertTrue(testInventory.take("Harry Potter").equals(OrderResult.SUCCESSFULLY_TAKEN));
        assertTrue(testInventory.take("Harry Potter").equals(OrderResult.NOT_IN_STOCK));
    }
}