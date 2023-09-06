package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.adapter.StringAdapter;
import org.apache.coyote.adapter.LoginAdapter;
import org.apache.coyote.adapter.ResourceAdapter;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestParser;
import org.apache.coyote.response.Response;
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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = new BufferedOutputStream(connection.getOutputStream())) {
            RequestParser requestParser = new RequestParser(inputStream);
            Request request = requestParser.parse();

            Response response = doHandler(request);

            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response doHandler(Request request) {
        if (request.isSamePath("/")) {
            return new StringAdapter().doHandle(request);
        }
        if (request.isSamePath("/login")) {
            return new LoginAdapter().doHandle(request);
        }
        return new ResourceAdapter().doHandle(request);
    }
}
