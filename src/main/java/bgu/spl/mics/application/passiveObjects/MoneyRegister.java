package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private BlockingQueue<OrderReceipt> orderReceipts;
	/**
     * Retrieves the single instance of this class.
     */
	private  static class SingletonHolder{
		private static MoneyRegister instance=new MoneyRegister();
	}
	private MoneyRegister(){
		this.orderReceipts = new LinkedBlockingQueue<>();
	}
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file(OrderReceipt r) {
		orderReceipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int total=0;
		BlockingQueue<OrderReceipt> curr=new LinkedBlockingQueue<OrderReceipt>();
		while (orderReceipts.size()!=0){
			OrderReceipt orderReceipt=orderReceipts.remove();
			total=total+orderReceipt.getPrice();
			curr.add(orderReceipt);
		}
		while (curr.size()!=0){orderReceipts.add(curr.remove());}
		return total;
	}
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) throws Exception {
		if (c.getAvailableCreditAmount() < amount){
			throw new Exception("Customer does not have enough money");
		}
		c.chargeCreditCard(amount);
	}

	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		//saving bookRemaining in a file
		List<OrderReceipt> FinalOrderReceipts = new LinkedList<OrderReceipt>();
		for (OrderReceipt o: orderReceipts) {
			FinalOrderReceipts.add(o);
		}
		try {
			//creating the hashmap of remaining books
			FileOutputStream outBooks = new FileOutputStream
					(filename);
			ObjectOutputStream out = new ObjectOutputStream(outBooks);
			// Method for serialization of object
			out.writeObject(FinalOrderReceipts);
			out.close();
			outBooks.close();
		}
		catch (IOException e){}
	}

	public BlockingQueue<OrderReceipt> getOrderReceipts() {
		return orderReceipts;
	}
}
