package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Pattern REQUEST_LINE_PATTERN =
            Pattern.compile("^(?<method>[A-Z]+) (?<uri>\\S+) (?<version>HTTP/\\d\\.\\d)$");
    private static final String URI = "uri";

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
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            Matcher matcher = REQUEST_LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                return;
            }

            String uri = matcher.group(URI);
            if (uri.equals("/")) {
                uri = "/index.html";
            }

            URL path = getClass().getClassLoader().getResource("static" + uri);
            if (path == null) {
                final var notFoundResponse = getResponseBody("404 Not Found", "text/html", "<h1>404 Not Found</h1>");
                outputStream.write(notFoundResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final var contentType = contentTypeOf(uri);
            final var content = Files.readString(Path.of(path.toURI()), StandardCharsets.UTF_8);

            final var responseBody = getResponseBody("200 OK", contentType, content);
            outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax: {}", e.getMessage(), e);
        }
    }

    public String getResponseBody(String status, String contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + bytes.length,
                "",
                content);
    }

    private String contentTypeOf(String path) {
        int dotIndex = path.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "" : path.substring(dotIndex);

        return switch (extension) {
            case ".css" -> "text/css";
            case ".js" -> "text/javascript";
            case ".ico" -> "image/x-icon";
            default -> "text/html";
        };
    }

}
