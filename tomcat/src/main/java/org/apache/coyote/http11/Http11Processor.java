package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.InvalidHttpRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.routing.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager manager;
    private final Dispatcher dispatcher;

    public Http11Processor(final Socket connection, Dispatcher dispatcher) {
        this(connection, new SessionManager(), dispatcher);
    }

    public Http11Processor(final Socket connection, final Manager manager, Dispatcher dispatcher) {
        this.connection = connection;
        this.manager = manager;
        this.dispatcher = dispatcher;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpResponse response = new HttpResponse();
            try {
                HttpRequest request = HttpRequest.from(reader, manager);
                dispatcher.doDispatch(request, response);
            } catch (InvalidHttpRequestException e) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setContentType("text/plain");
                response.setBody(e.getMessage());
            }

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

}
