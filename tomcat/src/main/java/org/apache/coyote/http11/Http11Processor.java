package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.mapper.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestHandler requestHandler;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.requestHandler = new HttpRequestHandler();
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

            HttpRequest request = requestHandler.handleRequest(inputStream);
            HttpResponse response = new HttpResponse(outputStream);
            response.setProtocol(request.getHttpProtocol());

            RequestMapping requestMapping = new RequestMapping();
            try {
                Controller controller = requestMapping.getController(request);
                controller.service(request, response);
            } catch (Exception e) {
                response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
                response.send();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
