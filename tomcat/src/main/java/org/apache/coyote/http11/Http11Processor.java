package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // request 파싱
            final List<String> request = getRequest(bufferedReader);

            final String startLine = request.getFirst();
            final String[] split = startLine.split(" ");
            final String requestTarget = split[1];
            final String response = getResponse(requestTarget);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static List<String> getRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            request.add(line);
        }
        return request;
    }

    private String getResponse(final String requestTarget) throws IOException {
        if (requestTarget.equals("/")) {
            final var responseBody = "Hello World!";

            return createResponse(responseBody, "text/html;charset=utf-8");
        }
        if (requestTarget.endsWith(".css")) {
            final var responseBody = getResponseBody(requestTarget);
            if (responseBody == null) {
                return "HTTP/1.1 404 NOT FOUND ";
            }

            return createResponse(responseBody, "text/css");
        }
        if (requestTarget.endsWith(".html")) {
            final var responseBody = getResponseBody(requestTarget);
            if (responseBody == null) {
                return "HTTP/1.1 404 NOT FOUND ";
            }

            return createResponse(responseBody, "text/html;charset=utf-8");
        }
        if (requestTarget.endsWith(".js")) {
            final var responseBody = getResponseBody(requestTarget);
            if (responseBody == null) {
                return "HTTP/1.1 404 NOT FOUND ";
            }

            return createResponse(responseBody, "text/javascript");
        }

        return "HTTP/1.1 404 NOT FOUND ";
    }

    private String getResponseBody(final String requestTarget) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + requestTarget);
        if (resource == null) {
            return null;
        }
        final Path path = Paths.get(resource.getPath());
        final List<String> contents = Files.readAllLines(path);

        return String.join("\r\n", contents);
    }

    private static String createResponse(
            final String responseBody,
            final String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
