package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.AuthController;
import nextstep.jwp.exception.UncheckedServletException;

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

            final HttpRequest request = new HttpRequest(inputStream);

            AuthController.login(request);

            final var response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest request) throws
        IOException,
        URISyntaxException {
        final String responseBody = getResponseBody(request);
        String contentType = "Content-Type: text/html;charset=utf-8 ";

        if (request.containsHeader("Accept") && request.getHeaderValue("Accept").contains("text/css")) {
            contentType = "Content-Type: text/css;";
        }

        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            contentType
            ,
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String getResponseBody(HttpRequest request) throws IOException, URISyntaxException {
        final URL requestUrl = request.getUrl(request.getUri());
        if (requestUrl.getPath().equals("/")) {
            return "Hello world!";
        }

        return new String(Files.readAllBytes(new File(Objects.requireNonNull(requestUrl).getFile()).toPath()));
    }
}
