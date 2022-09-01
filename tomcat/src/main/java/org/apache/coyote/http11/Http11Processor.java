package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_MESSAGE_DELIMITER = " ";
    private static final int RESOURCE_LOCATION = 1;
    private static final String STATIC_PATH = "static/";
    private static final String DEFAULT_CONTENT = "Hello world!";

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
             final var outputStream = connection.getOutputStream();
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {

            final String resource = parseRequestResource(bufferedReader);
            final String responseBody = loadResourceContent(resource);
            final String contentType = selectContentType(resource);
            final String response = createResponseMessage(responseBody, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestResource(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();

        return firstLine.split(HTTP_MESSAGE_DELIMITER)[RESOURCE_LOCATION];
    }

    private String loadResourceContent(final String resource) {
        final URL path = getClass().getClassLoader().getResource(STATIC_PATH + resource);

        final String content;
        try {
            content = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        } catch (IOException e) {
            return DEFAULT_CONTENT;
        }
        return content;
    }

    private String selectContentType(final String resource) {
        final String extension = resource.split("\\.")[1];
        if (extension.equals("css")) {
            return "text/css";
        }
        if (extension.equals("js")) {
            return "text/javascript";
        }
        if (extension.equals("svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }

    private static String createResponseMessage(final String responseBody, final String extension) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + extension + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
