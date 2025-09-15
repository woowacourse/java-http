package support;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHttp11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(TestHttp11Processor.class);
    private final Socket connection;

    public TestHttp11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            Thread.sleep(3000);
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }
}
