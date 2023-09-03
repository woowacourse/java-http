package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.request.URI;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser parser = new HttpRequestParser();
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest request = parser.parse(bufferedReader);
            StartLine startLine = request.startLine();
            if (startLine.isEmpty()) {
                return;
            }
            URI uri = startLine.uri();
            HttpResponse response;
            if (uri.path().equals("/")) {
                RootPageRequestHandler rootPageRequestHandler = new RootPageRequestHandler();
                response = rootPageRequestHandler.handle(request);
            } else if (uri.path().equals("/login")) {
                LoginRequestHandler loginRequestHandler = new LoginRequestHandler();
                response = loginRequestHandler.handle(request);
            } else {
                StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler();
                response = staticResourceRequestHandler.handle(request);
            }
            bufferedWriter.write(response.toString());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String contentType(URI uri) {
        if (uri.path().endsWith(".css")) {
            return "text/css;";
        }
        return "text/html;";
    }
}
