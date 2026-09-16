package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            final String[] lines = requestLine.split(" ");
            final String uri = lines[1];

            String headerLine = bufferedReader.readLine();
            while (headerLine != null && !headerLine.isEmpty()) {
                headerLine = bufferedReader.readLine();
                if (headerLine == null) {
                    return;
                }
            }

            var responseBody = "Hello world!";
            int contentLength = responseBody.getBytes().length;
            String contentType = "text/html;charset=utf-8";

            if (!uri.equals("/")) {
                byte[] fileBytes = readResource("static" + uri);

                if (fileBytes == null) {
                    return;
                }

                responseBody = new String(fileBytes, StandardCharsets.UTF_8);
                contentLength = fileBytes.length;
            }

            if (uri.endsWith(".css")) {
                contentType = "text/css";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + contentLength + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] readResource(String resourcePath) throws IOException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        URI resourceUri = resourceUrl.toURI();
        Path path = Paths.get(resourceUri);

        return Files.readAllBytes(path);
    }
}
