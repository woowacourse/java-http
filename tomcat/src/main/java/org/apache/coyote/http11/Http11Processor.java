package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var lines = new String(inputStream.readAllBytes()).split("\n");

            final var firstLine = lines[0];
            final var uri = firstLine.split(" ")[1];

            final var responseBody = makeResponseBody(uri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseBody(final String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }

        final var resource = getClass().getClassLoader().getResource("static" + uri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
