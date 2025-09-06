package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.ControllerHandler;
import org.apache.coyote.http11.handler.HandlerDispatcher;
import org.apache.coyote.http11.handler.HandlerMapping;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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

            /** HTTP Request Example (RFC 7230 기준)
             * GET /index.html HTTP/1.1
             * Host: localhost:8080
             * Connection: keep-alive
             * */
            HttpRequest request = new HttpRequest(inputStream);
            log.debug("request line: {}", request.getRequestLine());
            log.debug("request headers: {}", request.getHeaders());
            log.debug("request body: {}", request.getBody());

            final HandlerDispatcher handlerDispatcher = new HandlerDispatcher(List.of(
                    new StaticResourceHandler(),
                    new ControllerHandler(new HandlerMapping())
            ));
            HttpResponse response = handlerDispatcher.handle(request);
            log.debug("response status line: {}", response.getStatusLine());
            log.debug("response headers: {}", response.getHeaders());
//            log.debug("response body: {}", response.getBody());

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
