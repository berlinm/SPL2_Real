package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    @Test
    public void getInstance() {
        MessageBusImpl impl=new MessageBusImpl();
        Assert.assertEquals(impl.getClass(),MessageBus.class);
    }
    @Test
    public void subscribeEvent() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m = new ExampleEventHandlerService("Test", arr);
        ExampleEvent e = new ExampleEvent("Tom");
        messageBus.register(m);
        messageBus.subscribeEvent(e.getClass(), m);
        Future future = messageBus.sendEvent(e);

        try {
            Message msn = messageBus.awaitMessage(m);
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }

        m.complete(e, "true");
        assertEquals(future.get(), "true");

    }

    @Test
    public void subscribeBroadcast() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "brodcast";
        MicroService m1 = new ExampleBroadcastListenerService("testMicroService", arr);
        ExampleBroadcast e = new ExampleBroadcast("Berlin");
        messageBus.register(m1);
        messageBus.subscribeBroadcast(e.getClass(), m1);
        messageBus.sendBroadcast(e);
    }

    @Test
    public void complete() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m1 = new ExampleEventHandlerService("testMicroService", arr);
        messageBus.register(m1);
        ExampleEvent e = new ExampleEvent("Tom");
        messageBus.subscribeEvent(e.getClass(), m1);
        Future future = messageBus.sendEvent(e);

        try {
            messageBus.awaitMessage(m1);
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }

        m1.complete(e, "true");
        assertEquals(future.get(), "true");

    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
        Future future;
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m1 = new ExampleEventHandlerService("testMicroService", arr);
        ExampleEvent e = new ExampleEvent("aa");
        messageBus.register(m1);
        messageBus.subscribeEvent(e.getClass(), m1);
        future = messageBus.sendEvent(e);

        try {
            Message msn = messageBus.awaitMessage(m1);
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }

        m1.complete(e, "true");
        assertEquals(future.get(), "true");
    }

    @Test
    public void register() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m1 = new ExampleEventHandlerService("Test", arr);
        messageBus.register(m1);

        try {
            messageBus.awaitMessage(m1);
        } catch (Exception ex){
            String expectedMessage = "Service not registered";
            assertNotEquals( "Exception message must be correct", expectedMessage, ex.getMessage() );
        }
    }

    @Test
    public void unregister() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m1 = new ExampleEventHandlerService("Test", arr);
        messageBus.register(m1);
        messageBus.unregister(m1);

        try {
            messageBus.awaitMessage(m1);
        } catch (Exception ex){
            String expectedMessage = "Service not registered";
            assertEquals( "Exception must be correct", expectedMessage, ex.getMessage() );
        }
    }

    @Test
    public void awaitMessage() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        String[] arr = new String[1];
        arr[0] = "event";
        MicroService m1 = new ExampleEventHandlerService("testMicroService", arr);
        ExampleEvent e = new ExampleEvent("mmm");
        try {
            assertEquals("Service not registered",  messageBus.awaitMessage(m1));
        } catch (Exception ex){
            String expectedMessage = "Service not registered";
            assertEquals( "Exception must be correct", expectedMessage, ex.getMessage() );
        }
        messageBus.register(m1);
        messageBus.subscribeEvent(e.getClass(), m1);
        Future future = messageBus.sendEvent(e);

        try {
            Message msn = messageBus.awaitMessage(m1);
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }

        m1.complete(e, "true");
        assertEquals(future.get(), "true");

    }
}
}