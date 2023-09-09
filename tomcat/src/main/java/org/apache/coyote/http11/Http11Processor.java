package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.servlet.Page;
import org.apache.coyote.http11.servlet.Servlet;
import org.apache.coyote.http11.servlet.ServletFinder;
import org.apache.coyote.http11.util.StaticFileLoader;
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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest request = HttpRequest.create(bufferedReader);
            HttpResponse response = HttpResponse.create();

            Servlet servlet = ServletFinder.find(request);
            try {
                servlet.service(request, response);
            } catch (BadRequestException e) {
                handleBadRequest(response);
            } catch (MethodNotAllowedException e) {
                handleMethodNotAllowed(response, List.of());
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleBadRequest(HttpResponse response) throws IOException {
        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

        response.setStatusCode(StatusCode.BAD_REQUEST);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
    }

    private void handleMethodNotAllowed(HttpResponse response, List<HttpMethod> methods) throws IOException {
        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
        response.setAllow(methods);
    }
}
