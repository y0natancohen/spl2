package bgu.spl.mics.application.passiveObjects;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can addIfAbcent ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister {
	
	/**
     * Retrieves the single instance of this class.
     */

	AtomicInteger total;
    private static MoneyRegister theSingleton = null;
	private static Queue<OrderReceipt> orderReceipts;

	private MoneyRegister(){
		orderReceipts = new ConcurrentLinkedDeque<>();
		total = new AtomicInteger(0);
	}

	public static synchronized MoneyRegister getInstance(){
		if (MoneyRegister.theSingleton == null){
			MoneyRegister.theSingleton = new MoneyRegister();
		}
		return MoneyRegister.theSingleton;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
	    // todo sync here?
		orderReceipts.add(r);
		total.addAndGet(r.getPrice());
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return total.get();
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		// TODO: sync here?
        CreditCard card = c.getCreditCard();
        card.charge(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		//TODO: Implement this
	}
}
