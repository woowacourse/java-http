package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {
            HttpResponse response = new HttpResponse(outputStream);
            try {
                HttpRequest request = new HttpRequest(inputStream);
                Controller controller = requestMapping.getController(request);
                controller.service(request, response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setBody(new byte[0]);
            }
            response.send();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
