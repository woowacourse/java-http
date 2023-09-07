package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.io.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            log.info("process start");

            final var requestReader = new RequestReader(inputStream);
            final var request = requestReader.read();
            final var response = DispatcherServlet.service(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
