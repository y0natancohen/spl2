package bgu.spl.mics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> testFuture;

    @Before
    public void setUp() throws Exception {
        testFuture = new Future<>();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void successfullGet() {
        new Thread(() -> testFuture.resolve(6)).start();
        int integer = testFuture.get();
        Assert.assertEquals(6, integer);
    }

    @Test
    public void FailingGetWithTimeout() {
        assertNull(testFuture.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void SuccessfullGetWithTimeout() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                fail();
            }
            testFuture.resolve(6);
        }).start();
        int res = testFuture.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(6, res);
    }
}