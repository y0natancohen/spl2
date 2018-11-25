package bgu.spl.mics.application.passiveObjects;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {


    public static int getBookInventoryInfosSize() {
        return bookInventoryInfos.size();
    }

    /**
     * Retrieves the single instance of this class.
     */


    private static ArrayList<BookInventoryInfo> bookInventoryInfos;
    private static Inventory theSingleton = null;

    private Inventory() {}

    public static Inventory getInstance(){
        if (Inventory.theSingleton == null){
            Inventory.theSingleton = new Inventory();
        }
        return Inventory.theSingleton;
    }

    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
    public void load (BookInventoryInfo[ ] inventory ) {
        bookInventoryInfos.addAll(Arrays.asList(inventory));
    }

    /**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
    public OrderResult take (String book) {

        return null;
    }



    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public int checkAvailabiltyAndGetPrice(String book) {
        BookInventoryInfo bookInfo = getBookInfo(book);
        if ((bookInfo != null) && (bookInfo.getAmountInInventory() > 0)){
            return bookInfo.getPrice();
        }
        return -1;
    }

    private BookInventoryInfo getBookInfo(String bookName){
        for (BookInventoryInfo bookInfo :
                bookInventoryInfos) {
            if (bookInfo.getBookTitle().equals(bookName)){
                return bookInfo;
            }
        }
        return null;
    }

    /**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
    public void printInventoryToFile(String filename){
        //TODO: Implement this
    }
}
