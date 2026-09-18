package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
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
    private static final String QUERY_SEPARATOR = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

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

            String requestLine = reader.readLine();
            if (requestLine == null) { return; }

            String[] requestLineTokens = requestLine.split(" ");
            String uri = requestLineTokens[1];

            String responseBody = resolveResponseBody(uri);
            String contentType = resolveContentType(uri);

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }

        String path = "static" + uri;

        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            path = "static" + uri.substring(0, index);
            Map<String, String> params = new HashMap<>();
            for (String pair : uri.substring(index + 1).split("&")) {
                String[] keyValue = pair.split("=");
                params.put(keyValue[0], keyValue[1]);
            }
            InMemoryUserRepository.findByAccount(params.get("account"))
                    .ifPresent(user -> log.info("user: {}", user));
        }

        if (!path.contains(".")) {
            path += ".html";
        }

        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            return "404 Not Found";
        }

        return new String(Files.readAllBytes(Path.of(resource.getPath())));
    }

    private String resolveContentType (String uri) {
        if (uri.endsWith(".css")) {
            return "text/css";
        }
        if (uri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
