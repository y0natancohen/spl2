package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        Inventory inventory1 = Inventory.getInstance();
        Inventory inventory2 = Inventory.getInstance();
        assertEquals(inventory1, inventory2);

    }

    @Test
    public void load() {
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