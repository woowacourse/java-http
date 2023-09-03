package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private static final String DELIMITER = "\r\n";
    private static final String STATIC_DIRECTORY = "static";
    private static final String NOT_FOUND_VIEW = "/404.html";
    private static final String INDEX_URI = "/";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String firstLine = bufferedReader.readLine();
            final HttpRequest request = HttpRequest.of(firstLine);

            if (request.isGet()) {
                sendResponse(outputStream, request);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(final OutputStream outputStream, HttpRequest request) throws IOException {
        if (request.getUri().equals(INDEX_URI)) {
            request = HttpRequest.toIndex();
        }
        File file = readStaticFile(request.getUri());
        final String response = makeResponse(new String(Files.readAllBytes(file.toPath())), request);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String makeResponse(final String responseBody, final HttpRequest httpRequest) {
        return String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + httpRequest.getExtension() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private File readStaticFile(final String requestUri) {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + requestUri);
        if (resource == null) {
            resource = classLoader.getResource(STATIC_DIRECTORY + NOT_FOUND_VIEW);
        }
        return new File(resource.getFile());
    }
}
