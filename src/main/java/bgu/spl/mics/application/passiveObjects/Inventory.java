package bgu.spl.mics.application.passiveObjects;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.*;


/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can addIfAbcent ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

    private static class SingletonHolder {
        private static Inventory instance = new Inventory();
    }

    public static Inventory getInstance() {
        return SingletonHolder.instance;
    }

    public int getBookInventoryInfosSize() {
        return this.bookInventoryInfos.size();
    }

    /**
     * Retrieves the single instance of this class.
     */
    private Queue<BookInventoryInfo> bookInventoryInfos;


    private Inventory() {
        bookInventoryInfos = new ConcurrentLinkedDeque<>();
    }


    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(BookInventoryInfo[] inventory) {
        bookInventoryInfos = new ConcurrentLinkedDeque<>(Arrays.asList(inventory));
    }

    /**
     * Attempts to take one book from the store.
     * <p>
     *
     * @param book Name of the book to take from the store
     * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * The first should not change the state of the inventory while the
     * second should reduce by one the number of books of the desired type.
     */
    public OrderResult take(String book) {
        System.out.println("inside Inventory.take()");
        synchronized (getInstance()) {
            for (BookInventoryInfo bookInfo : bookInventoryInfos) {
                if (bookInfo.getBookTitle().equals(book) && bookInfo.getAmountInInventory() > 0) {
                    bookInfo.decreaseAmount();
                    return OrderResult.SUCCESSFULLY_TAKEN;
                }
            }
            return OrderResult.NOT_IN_STOCK;
        }

    }

    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     *
     * @param book Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public int checkAvailabiltyAndGetPrice(String book) {
        System.out.println("inside Inventory.checkAvailabiltyAndGetPrice()");
        synchronized (getInstance()) {
            BookInventoryInfo bookInfo = getBookInfo(book);
            if ((bookInfo != null) && (bookInfo.getAmountInInventory() > 0)) {
                return bookInfo.getPrice();
            }
            return -1;
        }
    }

    private BookInventoryInfo getBookInfo(String bookName) {
        for (BookInventoryInfo bookInfo :
                bookInventoryInfos) {
            if (bookInfo.getBookTitle().equals(bookName)) {
                return bookInfo;
            }
        }
        return null;
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */

    public void printInventoryToFile(String filename) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        File file = new File(filename);
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        try {
            synchronized (getInstance()) {
                for (BookInventoryInfo bookInfo : bookInventoryInfos) {
                    map.put(bookInfo.getBookTitle(), bookInfo.getAmountInInventory());
                }
                s.writeObject(map);
                s.close();
            }
        } finally {
            s.writeObject(map);
            s.close();
        }
    }
}
