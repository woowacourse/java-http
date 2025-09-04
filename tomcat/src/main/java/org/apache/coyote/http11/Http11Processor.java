package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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
             final var outputStream = connection.getOutputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var requestLine = reader.readLine();
            final var requestUrl = extractRequestUrlFrom(requestLine);

            final Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] parts = line.split(": ");
                if(parts.length >= 2) {
                    requestHeaders.put(parts[0], parts[1]);
                }
            }
            String mimeType = requestHeaders.getOrDefault("Accept", "text/html").split(",")[0];

            var responseBody = "Hello world!";
            if (!requestUrl.isBlank()) {
                var resource = getClass().getClassLoader().getResource("static/" + requestUrl);
                if (resource != null) {
                    var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + mimeType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestUrlFrom(final String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            return "";
        }

        final String[] parts = requestLine.split(" ");
        if(parts.length < 2) {
            return "";
        }
        return parts[1].substring(1);
    }
}
