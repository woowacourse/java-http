package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.handler.Handler;
import org.apache.coyote.httpresponse.handler.IndexHandler;
import org.apache.coyote.httpresponse.handler.LoginHandler;
import org.apache.coyote.httpresponse.handler.RegisterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, Handler> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("/", new IndexHandler());
        HANDLERS.put("/index.html", new IndexHandler());
        HANDLERS.put("/login", new LoginHandler());
        HANDLERS.put("/register", new RegisterHandler());
    }

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpRequest request = HttpRequest.from(inputStream);
            Handler handler = HANDLERS.get(request.getPath());
            if (handler == null) {
                handler = HANDLERS.get("/");
            }
            final HttpResponse response = handler.handle(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }
}
