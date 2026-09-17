package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.*;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
             final var outputStream = connection.getOutputStream()) {

            String requestUri = readRequestUri(reader);

            if (requestUri.equals("/index.html")) {
                serverIndexPage(reader, requestUri, outputStream);
                return;
            }
            if (requestUri.equals("/")) {
                serverHomePage(outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String readRequestUri(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        if (line == null) {
            throw new IOException("EOF: No request line received");
        }

        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid line received: " + line);
        }

        String requestUri = parts[1];
        return requestUri;
    }

    private void serverIndexPage(BufferedReader reader, String requestUri, OutputStream outputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        readRequestHeader(reader, headers);

        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri));

        Path path = Path.of(resource.getPath());

        byte[] responseBody = Files.readAllBytes(path);

        final var response = createHtmlOkResponseHeader(responseBody.length);

        writeResponse(outputStream, response.getBytes(UTF_8), responseBody);
    }

    private static void serverHomePage(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = createHtmlOkResponseHeader(responseBody.getBytes().length);

        writeResponse(outputStream, response.getBytes(), responseBody.getBytes(UTF_8));
    }

    private static void readRequestHeader(BufferedReader reader, Map<String, String> headers) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(" ");
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    }

    @Nonnull
    private static String createHtmlOkResponseHeader(int contentLength) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                ""
        );
    }

    private static void writeResponse(OutputStream outputStream, byte[] response, byte[] responseBody)
            throws IOException {
        outputStream.write(response);
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
