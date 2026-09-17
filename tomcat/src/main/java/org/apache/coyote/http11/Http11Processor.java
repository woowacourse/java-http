package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
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

            String url = parseRequestUrl(inputStream);
            String contentType = findContentType(url);

            byte[] responseBody = buildResponseBody(url);
            String response = buildResponse(responseBody, contentType);
            outputStream.write(response.getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponse(
            byte[] responseBody,
            String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");
    }

    private byte[] buildResponseBody(String url) throws URISyntaxException, IOException {
        if ("/".equals(url)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        String resourcePath = "static" + url;
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        Path path = Path.of(resource.toURI());
        return Files.readAllBytes(path);
    }

    private String parseRequestUrl(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String[] request = reader.readLine().trim().split("\\s+");
        return request[1];
    }

    private String findContentType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        }
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
