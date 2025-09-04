package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
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

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            final String firstLineOfHeader = br.readLine();
            final String requestUri = firstLineOfHeader.split(" ")[1];
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
            }

            final String contentType = getContentType(requestUri);
            final String responseBody = getResponseBody(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/%s;charset=utf-8 ".formatted(contentType),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "css";
        }
        return "html";
    }

    private String getResponseBody(final String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        final String fileName = "static" + requestUri;
        final URL resource  = getClass().getClassLoader().getResource(fileName);
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
