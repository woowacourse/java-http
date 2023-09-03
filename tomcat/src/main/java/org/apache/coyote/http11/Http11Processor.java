package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
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
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest httpRequest = HttpRequest.from(reader);

            final String responseBody = createResponseBody(httpRequest);
            final ContentType responseContentType = ContentType.findResponseContentTypeFromRequest(httpRequest);

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + responseContentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponseBody(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestOf(HttpMethod.GET, "/")) {
            return "Hello world!";
        }

        URL url = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (url == null) {
            url = getClass().getClassLoader().getResource("static/404.html");
            return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
        }

        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }
}
