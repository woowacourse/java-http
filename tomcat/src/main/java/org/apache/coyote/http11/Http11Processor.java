package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        try (InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {

            final var requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            final String[] tokens = requestLine.split(" ");
            final String requestTarget = tokens[1];

            log.debug("request line: {}", requestLine);

            String line;
            while ((line = reader.readLine()) != null && !line.equals("")) {
                log.debug("header : {}", line);
            }

            if (requestTarget.equals("/")) {
                respondHelloWorld(outputStream);

            } else if (requestTarget.endsWith(".css")) {
                respondCssHeader(requestTarget, outputStream);
            } else {
                respondStaticResource(requestTarget, outputStream);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void respondHelloWorld(OutputStream outputStream) {
        try {
            final var responseBody = "Hello world!";

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

    private void respondStaticResource(String requestTarget, OutputStream outputStream) {
        try (final var resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + requestTarget)) {

            final var responseBody = Objects.requireNonNull(resourceStream).readAllBytes();

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType(requestTarget),
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void respondCssHeader(String requestTarget, OutputStream outputStream) {
        try (final var resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + requestTarget)) {

            final var responseBody = Objects.requireNonNull(resourceStream).readAllBytes();

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType(requestTarget),
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String contentType(final String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css; charset=utf-8 ";
        }
        if (requestTarget.endsWith(".js")) {
            return "application/javascript; charset=utf-8 ";
        }
        if (requestTarget.endsWith(".svg")) {
            return "image/svg+xml ";
        }
        return "text/html;charset=utf-8 ";
    }
}
