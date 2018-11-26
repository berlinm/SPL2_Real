package bgu.spl.mics;

import org.junit.*;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

public class FutureTest {

    @Test
    public void get() {

        Future<String> stringFuture=new Future<String>();
        stringFuture.resolve("Tom");
        Assert.assertEquals("Tom",stringFuture.get());
        stringFuture.resolve("Berlin");
        Assert.assertEquals("Berlin",stringFuture.get());

        Future<Integer> IntegerFuture=new Future<Integer>();
        IntegerFuture.resolve(5);
        Assert.assertEquals(5,IntegerFuture.get().intValue());
    }

    @Test
    public void resolve() {
        Future<Boolean> booleanFuture=new Future<Boolean>();
        booleanFuture.resolve(true);
        Assert.assertNotNull(booleanFuture.get());
        Assert.assertEquals(true,booleanFuture.get());
        booleanFuture.resolve(false);
        Assert.assertEquals(false,booleanFuture.get());
    }

    @Test
    public void isDone() {
        Future<String> stringFuture=new Future<String>();
        Assert.assertEquals(false,stringFuture.isDone());
        stringFuture.resolve("Resolve");
        Assert.assertEquals(true,stringFuture.isDone());
    }

    @Test
    public void get1() {
    }
}