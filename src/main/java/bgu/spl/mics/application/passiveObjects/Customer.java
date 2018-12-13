package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may addIfAbcent fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private int id;
	private String name;
	private String address;
	private int distance;
	private CreditCard creditCard;
	private List<OrderSchedule> orderSchedule;
	private Queue<OrderReceipt> receipts;

    public CreditCard getCreditCard() {
        return this.creditCard;
    }

    public Customer(int id, String name, String address, int distance, CreditCard creditCard) {
        this.id = id;
        this.name = name;

        this.address = address;
        this.distance = distance;
        this.creditCard = creditCard;
    }

    public List<OrderSchedule> getOrderSchedule() {
        return orderSchedule;
    }

    public void setOrderSchedule(List<OrderSchedule> orderSchedule) {
        this.orderSchedule = orderSchedule;
    }

    /**
     * Retrieves the name of the customer.
     */
    public String getName() {
		return name;
	}

	public void initRecipts(){
        this.receipts = new ConcurrentLinkedDeque<>();
    }

    public Queue<OrderReceipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(Queue<OrderReceipt> receipts) {
        this.receipts = receipts;
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
		return new ArrayList<>(this.receipts);
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

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
