package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final Session session) {
        this.connection = connection;
        this.requestMapping = new RequestMapping(session);
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var input = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedOutputStream(connection.getOutputStream())) {

            HttpRequest request = new HttpRequest(input);
            HttpResponse response = new HttpResponse(out);

            processRequest(request, response);
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void processRequest(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = requestMapping.getController(request.getPath());
        controller.service(request, response);
    }

    private void handleError(Exception e) {
        try (HttpResponse response = new HttpResponse(new BufferedOutputStream(connection.getOutputStream()))) {
            log.error(e.getMessage());
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
