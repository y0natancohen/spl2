package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.junit.Assert.*;

public class InventoryTest {
    public Inventory inventory;
    public BookInventoryInfo[] bookInventoryInfos;
    public File tempFile;

    @Before
    public void setUp() throws Exception {
        Inventory inst = Inventory.getInstance();
        inventory = inst;

        bookInventoryInfos = new BookInventoryInfo[3];
        bookInventoryInfos[0] = new BookInventoryInfo("book1", 2, 100);
        bookInventoryInfos[1] = new BookInventoryInfo("book2", 20, 200);
        bookInventoryInfos[2] = new BookInventoryInfo("book3", 30, 300);
        inventory.load(bookInventoryInfos);

        tempFile = File.createTempFile("inventory-test-file", ".tmp");
    }


    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

    @Test
    public void getInstance() {
        Inventory inventory1 = Inventory.getInstance();
        Inventory inventory2 = Inventory.getInstance();
        assertSame(inventory1, inventory2);
    }

    @Test
    public void load() {
        assertEquals(3,inventory.getBookInventoryInfosSize());

        for (int i = 0; i < 3; i++) {
            String name = bookInventoryInfos[i].getBookTitle();
            assertEquals(bookInventoryInfos[i].getPrice(),
                    inventory.checkAvailabiltyAndGetPrice(name));
        }
    }

    @Test
    public void take() {
        OrderResult or1 = inventory.take("book1");
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN.name(), or1.name());
        OrderResult or11 = inventory.take("book1");
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN.name(), or11.name());
        OrderResult or111 = inventory.take("book1");
        assertEquals(OrderResult.NOT_IN_STOCK.name(), or111.name());

        OrderResult or2 = inventory.take("book23");
        assertEquals(OrderResult.NOT_IN_STOCK.name(), or2.name());

        OrderResult or3 = inventory.take("book2");
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN.name(), or3.name());


    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        assertEquals(100, inventory.checkAvailabiltyAndGetPrice("book1"));
        assertEquals(200, inventory.checkAvailabiltyAndGetPrice("book2"));
        assertEquals(300, inventory.checkAvailabiltyAndGetPrice("book3"));
        assertEquals(-1, inventory.checkAvailabiltyAndGetPrice("book4"));
    }

    @Test
    public void printInventoryToFile() {
        String filename = tempFile.getAbsolutePath();
        try {
            inventory.printInventoryToFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        JSONParser parser = new JSONParser();
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                map = (HashMap) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                ois.close();
                fis.close();
            }
//            JSONObject json = (JSONObject) parser.parse(new FileReader(filename));

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()){
            String bookName = entry.getKey();
            int bookAmount = entry.getValue();
            assertEquals(inventory.getAmount(bookName),bookAmount);
        }


    }
}