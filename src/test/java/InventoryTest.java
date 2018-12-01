import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class InventoryTest {
    public Inventory inventory;
    public BookInventoryInfo[] bookInventoryInfos;

    @Before
    public void setUp() {
        Inventory inst = Inventory.getInstance();
        inventory = inst;

        bookInventoryInfos = new BookInventoryInfo[3];
        bookInventoryInfos[0] = new BookInventoryInfo("book1", 2, 100);
        bookInventoryInfos[1] = new BookInventoryInfo("book2", 20, 200);
        bookInventoryInfos[2] = new BookInventoryInfo("book3", 30, 300);
        inventory.load(bookInventoryInfos);

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

}