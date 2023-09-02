package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

import static org.apache.coyote.http11.ContentType.ALL;
import static org.apache.coyote.http11.ContentType.APPLICATION_JAVASCRIPT;
import static org.apache.coyote.http11.ContentType.TEXT_CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;

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

            var requestReader = new RequestReader(inputStream);
            String resource = requestReader.read();
            String acceptValue = requestReader.getAccept();

            var response = getResponse(resource, acceptValue);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getResponse(String resource, String acceptValue) throws IOException {
        var response = new Response(ALL, resource).getResponse();

        if (TEXT_HTML.in(acceptValue)) {
            response = new Response(TEXT_HTML, resource).getResponse();
        } else if (TEXT_CSS.in(acceptValue)) {
            response = new Response(TEXT_CSS, resource).getResponse();
        } else if (APPLICATION_JAVASCRIPT.in(acceptValue)) {
            response = new Response(APPLICATION_JAVASCRIPT, resource).getResponse();
        }
        return response;
    }
}
