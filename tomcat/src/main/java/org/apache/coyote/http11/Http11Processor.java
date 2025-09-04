package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.DefaultHandler;
import com.techcourse.handler.LoginHandler;
import com.techcourse.handler.RootHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpRequestParser;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<String, HttpRequestHandler> handlerMap;
    private final HttpRequestHandler defaultHandler;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerMap = new HashMap<>();
        handlerMap.put("/", new RootHandler());
        handlerMap.put("/login", new LoginHandler());
        this.defaultHandler = new DefaultHandler();
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

            final HttpRequest request = HttpRequestParser.parseRequest(inputStream);
            final HttpResponse response = new HttpResponse(PROTOCOL);

            if (handlerMap.containsKey(request.getPath())) {
                handleRequest(request, response);
                writeResponse(response, outputStream);
                return;
            }

            defaultHandler.handleGet(request, response);
            writeResponse(response, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final HttpResponse response, OutputStream outputStream) {
        try {
            outputStream.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        final HttpRequestHandler handler = handlerMap.get(request.getPath());
        switch (request.getMethod()) {
            case "GET" -> handler.handleGet(request, response);
            default -> throw new UnsupportedOperationException("지원하지 않는 요청 방식입니다.");
        }
    }
}
