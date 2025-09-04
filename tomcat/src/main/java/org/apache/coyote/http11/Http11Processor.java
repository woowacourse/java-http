package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_PATH = "static";

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            final var requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            final var requestLines = requestLine.split(" ");
            final var resourcePath = requestLines[1];
            final var headers = getHeaders(bufferedReader);
            log.info("resourcePath: {}, headers: {}", resourcePath, headers);

            handleRequest(resourcePath, outputStream);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            final String[] header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }
        return headers;
    }

    private void handleRequest(String resourcePath, OutputStream outputStream)
            throws IOException, URISyntaxException {
        if (resourcePath.equals("/")) {
            outputStream.write(buildRootResponse());
            return;
        }

        final var resource = getClass().getClassLoader().getResource(STATIC_PATH + resourcePath);
        final var filePath = Paths.get(Objects.requireNonNull(resource).toURI());

        final var contentType = getContentType(resourcePath);
        final var responseBody = Files.readAllBytes(filePath);

        outputStream.write(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "").getBytes());
        outputStream.write(responseBody);
    }

    private byte[] buildRootResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!").getBytes();
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        if (resourcePath.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "text/html;charset=utf-8";
    }
}
