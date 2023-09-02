package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Request request = RequestExtractor.extract(inputStream);
            Response response = generateResponse(request);
            logUserInfoIfExists(request);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response generateResponse(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        String resource = findResourceWithPath(requestURI.absolutePath());
        String contentType = ContentTypeParser.parse(requestURI.absolutePath());
        int contentLength = resource.getBytes().length;

        return Response.from(request.getHttpVersion(), "200 OK",
                contentType, contentLength, resource);
    }

    private String findResourceWithPath(String absolutePath) throws IOException {
        if (absolutePath.equals("/")) {
            return "Hello world!";
        }

        if (hasNoExtension(absolutePath)) {
            absolutePath += ".html";
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + absolutePath);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }

    private boolean hasNoExtension(String absolutePath) {
        return !absolutePath.contains(".");
    }

    private void logUserInfoIfExists(Request request) {
        RequestURI requestURI = request.getRequestURI();

        if (requestURI.hasQueryParameters()) {
            String[] queryParameters = requestURI.queryParameters();
            log.info(queryParameters[0]);
            log.info(queryParameters[1]);
        }
    }
}
