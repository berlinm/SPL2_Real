package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class BookSemaphoreHolder {
    private static BookSemaphoreHolder instance = new BookSemaphoreHolder();
    ConcurrentHashMap<String, Semaphore> bookLocker;
    private BookSemaphoreHolder(){
        this.bookLocker = new ConcurrentHashMap<>();
    }
    public void add(String bookTitle, int permits){
        this.bookLocker.putIfAbsent(bookTitle, new Semaphore(permits));
    }
    public static BookSemaphoreHolder getInstance(){
        return instance;
    }
    public synchronized boolean tryAcquire(String bookName){
        return this.bookLocker.get(bookName).tryAcquire();
    }
    public synchronized void acquire(String bookName){
        try {
            this.bookLocker.get(bookName).acquire();
        }
        catch (InterruptedException e){}
    }
    public synchronized void release(String bookName){
        this.bookLocker.get(bookName).release();
    }
}
