package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.BaseHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.StaticFileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest httpRequest = RequestGenerator.generate(reader);
            HttpResponse response = handleHttpRequest(httpRequest);
            writer.write(response.toString());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        String requestURI = httpRequest.getRequestLine().getRequestURI();
        if (requestURI.equals("/")) {
            return BaseHandler.handle();
        }
        if (requestURI.startsWith("/login")) {
            return LoginHandler.handle(httpRequest);
        }
        if (requestURI.startsWith("/register")) {
            return RegisterHandler.handle(httpRequest);
        }
        return StaticFileHandler.handle(requestURI, httpRequest);
    }
}
