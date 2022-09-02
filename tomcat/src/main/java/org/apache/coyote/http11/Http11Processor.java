package org.apache.coyote.http11;

import http.BasicHttpRequest;
import http.HttpRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.ui.HomeController;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HomeController homeController;

    public Http11Processor(final Socket connection, final HomeController homeController) {
        this.connection = connection;
        this.homeController = homeController;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var httpRequest = parseRequest(inputStream);
            final var request = BasicHttpRequest.from(httpRequest);
            final var contentType = request.getContentType();

            final var responseBody = getResponseBodyByURI(request);
            final var response = createResponseWithBody(responseBody, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(final InputStream inputStream) throws IOException {
        final var request = new StringBuilder();

        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (bufferedReader.ready()) {
            request.append(String.format("%s%s", bufferedReader.readLine(), System.lineSeparator()));
        }

        return request.toString();
    }

    private String getResponseBodyByURI(final HttpRequest request) throws IOException {
        var requestURI = request.getRequestURI();

        if (!requestURI.contains(".")) {
            return homeController.service(request);
        }

        final var resource = getClass().getClassLoader().getResource(String.format("static%s", requestURI));
        final var path = new File(resource.getFile()).toPath();

        return new String(Files.readAllBytes(path));
    }

    private String createResponseWithBody(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
