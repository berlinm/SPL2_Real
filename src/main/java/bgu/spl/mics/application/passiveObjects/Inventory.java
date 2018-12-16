package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.BookSemaphoreHolder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private ConcurrentLinkedQueue<BookInventoryInfo> bookInventoryInfos;
	private static class SingletonHolder{
		private static Inventory instance = new Inventory();
	}
	/**
     * Retrieves the single instance of this class.
     */

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load(BookInventoryInfo[] inventory) {
		bookInventoryInfos =new ConcurrentLinkedQueue<BookInventoryInfo>();
		for(int i = 0; i < inventory.length; i++){
			bookInventoryInfos.add(inventory[i]);
		}
	}
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take(String book) {
		Iterator<BookInventoryInfo> it = bookInventoryInfos.iterator();
		while (it.hasNext()) {
			BookInventoryInfo curr = it.next();
			if (curr.getBookTitle() == book) {
				if (curr.getAmountInInventory() > 0) {
					try {
						curr.takeBook();
					} catch (RuntimeException bookNotInStock) {
						return OrderResult.NOT_IN_STOCK;
					}
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		int price = -1;
		Iterator<BookInventoryInfo> it = bookInventoryInfos.iterator();
		while (it.hasNext()){
			BookInventoryInfo curr = it.next();
			if(curr.getBookTitle().equals(book) && curr.getAmountInInventory() > 0){
				price = curr.getPrice();
			}
		}
		if (price != -1){ //&& BookSemaphoreHolder.getInstance().tryAcquire(book)){
			return price;
		}
		return -1;
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
		//saving bookRemaining in a file
		try {
			//creating the hashmap of remaining books
			HashMap<String, Integer> BooksRemaining = new HashMap<String, Integer>();
			for (BookInventoryInfo b: bookInventoryInfos) {
				BooksRemaining.put(b.getBookTitle(), b.getAmountInInventory());
			}
			FileOutputStream outBooks = new FileOutputStream
					(filename);
			ObjectOutputStream out = new ObjectOutputStream(outBooks);
			// Method for serialization of object
			out.writeObject(BooksRemaining);
			out.close();
			outBooks.close();
		}
		catch (IOException e){}
	}
	//returns true if there is no book types reserved in the inventory, does not tell anything about how many books there are
	public boolean isEmpty(){
		return this.bookInventoryInfos.isEmpty();
	}
	public int getTotalNumberOfBooks(){
		int total = 0;
		for (BookInventoryInfo book : this.bookInventoryInfos){
			total += book.getAmountInInventory();
		}
		return total;
	}
}