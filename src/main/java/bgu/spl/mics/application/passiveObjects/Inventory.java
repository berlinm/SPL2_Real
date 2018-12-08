package bgu.spl.mics.application.passiveObjects;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.print.Book;
import java.util.concurrent.ConcurrentHashMap;

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
	private BookInventoryInfo[] bookInventoryInfos;

	/**
     * Retrieves the single instance of this class.
     */
	private  static class SingletonHolder{
		private static Inventory instance=new Inventory();

	}
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
	public void load (BookInventoryInfo[ ] inventory ) {
		bookInventoryInfos=new BookInventoryInfo[inventory.length];

		for(int i=0;i<inventory.length;i++){
			bookInventoryInfos[i]=inventory[i];
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
	public OrderResult take (String book) {
		for(int i=0;i<bookInventoryInfos.length;i++){
			if(bookInventoryInfos[i].getBookTitle()==book){
				if(bookInventoryInfos[i].getAmountInInventory()>0){
					return OrderResult.SUCCESSFULLY_TAKEN;
				}else {
					return OrderResult.NOT_IN_STOCK;
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

		for(int i=0;i<bookInventoryInfos.length;i++){
			if(bookInventoryInfos[i].getBookTitle()==book){
				return bookInventoryInfos[i].getPrice();
			}
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
		//TODO: Implement this
	}
	public boolean isEmpty(){
		throw new NotImplementedException();
	}
	public BookInventoryInfo getBookInventoryInfo(int i){
		throw new NotImplementedException();
	}
	public int getTotalNumberOfBooks(){
		throw new NotImplementedException();
	}
}
