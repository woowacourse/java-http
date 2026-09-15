package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.coyote.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final Dispatcher dispatcher;

    public Http11Processor(final Socket connection, final Dispatcher dispatcher) {
        this.connection = connection;
        this.dispatcher = dispatcher;
    }

    public Http11Processor(final Socket connection) {
        this(connection, (path, params) -> java.util.Optional.empty());
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
             final var reader = new BufferedReader(new InputStreamReader(inputStream));) {

            String rawRequest = reader.readLine();
            rawRequest = rawRequest.split(" ")[1];

            URI uri = URI.create(rawRequest);

            String request = uri.getPath();
            String rawQuery = uri.getRawQuery();
            var dispatchedPath = dispatcher.dispatch(request, getQuery(rawQuery));
            String responsePath = dispatchedPath.orElse(request);

            String responseBody = "Hello world!";
            log.info("request: {}", request);

            if (!"/".equals(responsePath)) {
                String resourcePath = "static" + responsePath;

                try (InputStream resource =
                             getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                    responseBody = new String(
                            Objects.requireNonNull(resource).readAllBytes(),
                            StandardCharsets.UTF_8
                    );

                }
            }

            byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            String contentType = getContentType(responsePath);
            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBodyBytes.length,
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getQuery(String rawQuery) {
        Map<String, String> query = new HashMap<>();
        if (rawQuery != null) {
            String[] params = rawQuery.split("&");
            for (String param : params) {
                String[] kv = param.split("=", 2);
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = kv.length > 1
                        ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                        : "";
                query.put(key, value);
            }
        }

        return query;
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}

