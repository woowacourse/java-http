package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.handler.BasicHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String STATIC_PATH = "static";
    private static final String LOGIN_PATH = "/login.html";
    private static final String REGISTER_PATH = "/register.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String INTERNAL_SERVER_PATH = "/500.html";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(br);
            final HttpResponse response = getRespond(httpRequest);
            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getRespond(final HttpRequest httpRequest) {
        try {
            return interceptHandler(httpRequest);
        } catch (NotFoundAccountException | NotCorrectPasswordException e) {
            return HttpResponse.foundResponse(httpRequest, UNAUTHORIZED_PATH);
        } catch (ResourceNotFoundException e) {
            return HttpResponse.foundResponse(httpRequest, NOT_FOUND_PATH);
        } catch (Exception e) {
            return HttpResponse.foundResponse(httpRequest, INTERNAL_SERVER_PATH);
        }
    }

    private HttpResponse interceptHandler(final HttpRequest httpRequest) throws IOException {
        final String url = httpRequest.getAbsolutePath();

        if (url.equals(ROOT_PATH)) {
            return BasicHandler.handle(httpRequest);
        }
        if (url.equals(LOGIN_PATH)) {
            return LoginHandler.handle(httpRequest);
        }
        if (url.equals(REGISTER_PATH)) {
            return RegisterHandler.handle(httpRequest);
        }

        final String responseBody = ResourceReader.readResource(STATIC_PATH + url);
        return HttpResponse.okResponse(httpRequest, responseBody);
    }
}
