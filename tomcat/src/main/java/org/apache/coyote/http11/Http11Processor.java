package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Map<String, String> contentType = Map.of(
            "css", "Content-Type: text/css;charset=utf-8 ",
            "html", "Content-Type: text/html;charset=utf-8 ",
            "js", "Content-Type: "
    );

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            final var request = reader.readLine().trim();

            String[] requestLineParts = request.split(" ");

            String method = requestLineParts[0];
            String requestPath = requestLineParts[1];

            if (isGetMethod(method)) {
                if (requestPath.equals("/")) {
                    final var response = getResponse();

                    sendResponse(outputStream, response);
                    return;
                }

                serveStaticFile(requestPath, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serveStaticFile(String requestPath, OutputStream outputStream) throws IOException {
        final var path = getPath(requestPath);
        final var responseBody = Files.readString(path);
        final var response = getResponse(responseBody, path);

        sendResponse(outputStream, response);
    }

    private Path getPath(String requestPath) {
        var resource = getClass().getClassLoader().getResource("static/" + requestPath);
        return Path.of(Objects.requireNonNullElseGet(
                resource, () -> Objects.requireNonNull(
                        getClass().getClassLoader().getResource("static/404.html")
                )
        ).getPath());
    }

    private String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    private String getResponse(String responseBody, Path path) throws IOException {
        if (path.toAbsolutePath().endsWith("404.html")) {
            responseBody = "404 NOT FOUND";
        }
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private boolean isGetMethod(String method) {
        return method.equals("GET");
    }
}
