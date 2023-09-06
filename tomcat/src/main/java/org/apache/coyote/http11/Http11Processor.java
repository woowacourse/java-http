package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.MemberRegisterHandler;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static";
    private final Socket connection;
    private final List<RequestHandler> handlers = new ArrayList<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;

        final RequestHandler loginHandler = new LoginHandler("/login");
        final RequestHandler memberRegisterHandler = new MemberRegisterHandler("/register");
        handlers.add(loginHandler);
        handlers.add(memberRegisterHandler);
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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            final Http11Response http11Response = handleRequest(httpRequest);

            final String response = http11Response.getResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response handleRequest(final HttpRequest httpRequest) {
        for (RequestHandler handler : handlers) {
            if (handler.isMatch(httpRequest)) {
                return handler.doService(httpRequest);
            }
        }

        final String requestUri = httpRequest.getRequestLine().getRequestURI();
        final String resourcePath = RESOURCE_PATH + requestUri;
        return new Http11Response(getClass().getClassLoader().getResource(resourcePath), 200, "OK");
    }
}
