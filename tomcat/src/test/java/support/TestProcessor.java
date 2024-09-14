package support;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class TestProcessor extends Http11Processor {

    private final CountDownLatch latch;

    public TestProcessor(Socket connection, Manager manager, CountDownLatch latch) {
        super(connection, manager);
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
