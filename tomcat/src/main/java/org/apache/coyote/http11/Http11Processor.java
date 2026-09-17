package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();

            if(requestLine == null) {
                return;
            }
            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            final String requestUri = requestParts[1];
            final String version = requestParts[2];
            final Map<String, String> headers = readHeaders(reader);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String type = "html";
            if (requestUri.equals("/index.html")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals("/css/styles.css")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
                type="css";
            }

            if (requestUri.equals("/js/scripts.js")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
                type = "javascript";
            }

            if (requestUri.equals("/assets/chart-area.js")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
                type = "javascript";
            }

            if (requestUri.equals("/assets/chart-bar.js")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
                type = "javascript";
            }

            if (requestUri.equals("/assets/chart-pie.js")) {
                final Path path = Path.of(getResourcePath("static" + requestUri));
                responseBody = Files.readAllBytes(path);
                type = "javascript";
            }

            final String responseHeader = createResponseHeader(version, type, responseBody.length);

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            final String name =  header[0].trim();
            final String value =  header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String createResponseHeader(final String version, String type, final int contentLength) {
        return String.join("\r\n",
                version + " 200 OK ",
                "Content-Type: text/"+ type + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
    }

    private String getResourcePath(String path) {
        return getClass().getClassLoader()
                .getResource(path)
                .getPath();
    }
}
