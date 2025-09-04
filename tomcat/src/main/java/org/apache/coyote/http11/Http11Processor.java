package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.InputStream;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            Http11Request http11Request = new Http11Request(inputStream);

            String resourcePath = http11Request.getUri().substring(1);
            byte[] body = readFromResourcePath(resourcePath);
            byte[] responseHeader = createResponseHeader(http11Request, body.length);

            outputStream.write(responseHeader);
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] createResponseHeader(final Http11Request http11Request, final int length) {
        String accept = parseAccept(http11Request);

        String header = "HTTP/1.1 200 OK" + " \r\n" +
        "Content-Type: " + accept + ";charset=utf-8" + " \r\n" +
        "Content-Length: " + length + " \r\n" +
        "\r\n";

        return header.getBytes();
    }

    private String parseAccept(final Http11Request http11Request) {
        String accept = http11Request.getHeader("Accept");

        if(accept.contains("text/html")) {
            return "text/html";
        }
        if (accept.contains("text/css")) {
            return "text/css";
        }
        return "*/*";
    }

    private byte[] readFromResourcePath(final String resourcePath) throws IOException {
        if(resourcePath.isEmpty()) {
            String response = "Hello world!";

            return response.getBytes();
        }

        String classPath = "static/" + resourcePath;

        try (InputStream resourceAsStream = getClass().
                getClassLoader().
                getResourceAsStream(classPath)) {
            return resourceAsStream.readAllBytes();
        }
    }
}
