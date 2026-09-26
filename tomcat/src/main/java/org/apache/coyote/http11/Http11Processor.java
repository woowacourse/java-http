package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

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
            HttpRequest request = HttpRequest.parse(inputStream);
            Controller controller = requestMapping.getController(request);
            HttpResponse response = controller.handle(request);

            response.writeTo(outputStream);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
