package org.apache.coyote.http11;

import static org.apache.coyote.http11.data.SessionManager.JSESSIONID_COOKIE_NAME;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.config.TomcatServerConfiguration;
import org.apache.coyote.http11.data.Cookie;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.Session;
import org.apache.coyote.http11.handle.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<RequestHandler> requestHandlers;

    public Http11Processor(final Socket connection) {
        this(connection, TomcatServerConfiguration.REQUEST_HANDLERS);
    }

    public Http11Processor(final Socket connection, final List<RequestHandler> requestHandlers) {
        this.connection = connection;
        this.requestHandlers = requestHandlers;
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

            final HttpResponse response = HttpResponse.create();
            final HttpRequest request;
            try {
                request = HttpRequest.from(inputStream);
            } catch (InvalidHttpRequestException e) {
                response.badRequest();
                response.setHeader("Connection", "close");
                outputStream.write(response.toString().getBytes());
                outputStream.flush();
                return;
            }

            handleRequest(request, response);

            final Session session = request.getSession(false);
            if (session != null) {
                Cookie cookie = Cookie.create(JSESSIONID_COOKIE_NAME, session.getId(), "/");
                response.getCookies().addCookie(cookie);
            }

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        for (RequestHandler requestHandler : requestHandlers) {
            if (requestHandler.canHandle(request)) {
                requestHandler.handle(request, response);
                return;
            }
        }

        response.badRequest();
    }
}
