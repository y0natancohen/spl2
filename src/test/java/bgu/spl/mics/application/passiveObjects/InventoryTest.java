package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    public Inventory inventory;

    @Before
    public void setUp() throws Exception {
        Inventory inst = Inventory.getInstance();
        inventory = inst;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        Inventory inventory1 = Inventory.getInstance();
        Inventory inventory2 = Inventory.getInstance();
        assertSame(inventory1, inventory2);
    }

    @Test
    public void load() {
        BookInventoryInfo[] bookInventoryInfos = new BookInventoryInfo[3];
        bookInventoryInfos[0] = new BookInventoryInfo("book1", 10, 100);
        bookInventoryInfos[1] = new BookInventoryInfo("book2", 20, 200);
        bookInventoryInfos[2] = new BookInventoryInfo("book3", 30, 300);
        inventory.load(bookInventoryInfos);
        assertEquals(3,inventory.getBookInventoryInfosSize());


        int[] expected_prices = {100, 200, 300};
        int[] actual_prices = new int[3];
        for (int i = 0; i < 3; i++) {
            String name = bookInventoryInfos[i].getBookTitle();
            assertEquals(bookInventoryInfos[i].getPrice(),
                    inventory.checkAvailabiltyAndGetPrice(name));
        }
    }

    @Test
    public void take() {
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
    }

    @Test
    public void printInventoryToFile() {
    }
}