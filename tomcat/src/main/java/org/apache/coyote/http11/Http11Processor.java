package org.apache.coyote.http11;

import nextstep.jwp.controller.ErrorHandler;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.core.MappingHandler;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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

            final HttpResponse response = new HttpResponse();
            invokeServlet(inputStream, response);

            final byte[] bytes = response.getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    private void invokeServlet(final InputStream inputStream, final HttpResponse response) throws IOException {
        try {
            final HttpRequest request = RequestMapper.toRequest(inputStream);
            final Controller controller = MappingHandler.getController(request);

            controller.service(request, response);
        } catch (final UncheckedServletException e) {
            ErrorHandler.handle(e, response);
        } catch (final IllegalArgumentException e) {
            ErrorHandler.handle(e, response);
        } catch (final Exception e) {
            ErrorHandler.handle(e, response);
        }
    }
}
