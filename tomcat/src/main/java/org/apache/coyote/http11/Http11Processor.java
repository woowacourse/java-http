package org.apache.coyote.http11;

import java.io.InputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();
    private static final HttpErrorHandler HTTP_ERROR_HANDLER = new HttpErrorHandler();

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
            processRequest(inputStream).write(outputStream);
        } catch (Exception e) {
            log.error("HTTP 응답을 전송하지 못했습니다.", e);
        }
    }

    private HttpResponse processRequest(final InputStream inputStream) {
        final HttpRequest request;
        try {
            request = new HttpRequest(inputStream);
        } catch (Exception e) {
            return HTTP_ERROR_HANDLER.handle(DEFAULT_HTTP_VERSION, e);
        }
        return createResponse(request);
    }

    private HttpResponse createResponse(final HttpRequest request) {
        try {
            final HttpResponse response = new HttpResponse(request.getHttpVersion());
            final Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);
            return response;
        } catch (Exception e) {
            return HTTP_ERROR_HANDLER.handle(request.getHttpVersion(), e);
        }
    }
}
