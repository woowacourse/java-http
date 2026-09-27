package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String INTERNAL_SERVER_ERROR_PAGE = "/500.html";

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
            final HttpRequest request;
            try {
                request = HttpRequest.from(inputStream);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
                sendBadRequest(outputStream);
                return;
            }
            if (request == null) {
                return;
            }

            final HttpResponse response = new HttpResponse();
            log.debug("{} {} 요청을 받았습니다.", request.getMethod(), request.getPath());

            service(request, response, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendBadRequest(final OutputStream outputStream) throws IOException {
        final HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.write(outputStream);
    }

    private void service(final HttpRequest request, final HttpResponse response, final OutputStream outputStream)
            throws IOException {
        try {
            final Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            response.write(outputStream);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            final HttpResponse errorResponse = new HttpResponse();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.sendStaticResource(INTERNAL_SERVER_ERROR_PAGE);
            errorResponse.write(outputStream);
        }
    }
}
