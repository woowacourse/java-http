package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String[] request = reader.readLine().trim().split("\\s+");
            String url = request[1];
            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String response = buildResponse(responseBody, "text/html");
            if (url.equals("/index.html")) {
                responseBody = buildResponseBody("static/index.html");
                response = buildResponse(responseBody, "text/html");
            } else if (url.contains("css")) {
                responseBody = buildResponseBody("static/css/styles.css");
                response = buildResponse(responseBody, "text/css");
            }

            outputStream.write(response.getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] buildResponseBody(String resourcePath) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        Path path = Path.of(resource.toURI());
        final InputStream htmlInputStream = new BufferedInputStream(Files.newInputStream(path));
        return htmlInputStream.readAllBytes();
    }

    private String buildResponse(byte[] responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");
    }
}
