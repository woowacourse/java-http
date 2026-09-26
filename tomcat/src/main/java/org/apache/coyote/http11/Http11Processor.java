package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

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
        try (final var inputStream = connection.getInputStream(); final var outputStream = connection.getOutputStream()) {
            final HttpRequest request = new HttpRequest(inputStream);
            final HttpResponse response = new HttpResponse(request.getHttpVersion());
            final Controller controller = REQUEST_MAPPING.getController(request);

            controller.service(request, response);
            response.write(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
