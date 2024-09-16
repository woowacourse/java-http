package support;

import java.net.Socket;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlowHttp11Processor extends Http11Processor {

    private static final Logger log = LoggerFactory.getLogger(org.apache.coyote.http11.Http11Processor.class);

    private final int waitingTime;

    public SlowHttp11Processor(Socket connection, Adapter adapter, int waitingTime) {
        super(connection, adapter);
        this.waitingTime = waitingTime;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        try {
            Thread.sleep(waitingTime);
        } catch (Exception e) {
        }
        process(connection);
    }
}
