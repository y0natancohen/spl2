package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may addIfAbcent fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	/**
     * Retrieves the name of the customer.
     */

	private int id;
	private String name;
	private String address;
	private int distance;
	private CreditCard creditCard;

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public Customer(int id, String name, String address, int distance, CreditCard creditCard) {
        this.id = id;
        this.name = name;

        this.address = address;
        this.distance = distance;
        this.creditCard = creditCard;
    }

    public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		// TODO Implement this
		return null;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditCard.getAmount();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCard.getNumber();
	}
	
}
