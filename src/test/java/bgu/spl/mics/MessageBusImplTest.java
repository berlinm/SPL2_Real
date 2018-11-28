package bgu.spl.mics;

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

    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }
}