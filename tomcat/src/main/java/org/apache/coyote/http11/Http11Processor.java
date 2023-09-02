package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProvider resourceProvider;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceProvider = new ResourceProvider();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final var outputStream = connection.getOutputStream()) {

            String requestLine = inputReader.readLine();
            String requestURI = requestURIOf(requestLine);

            var responseBody = "Hello world";
            var contentType = "Content-Type: text/html;charset=utf-8 ";

            if (resourceProvider.haveResource(requestURI)) {
                responseBody = resourceProvider.resourceBodyOf(requestURI);
                contentType = resourceProvider.contentTypeOf(requestURI);
            }
            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String requestURIOf(String requestLine) {
        String[] split = requestLine.split(" ");
        return split[1];
    }
}
