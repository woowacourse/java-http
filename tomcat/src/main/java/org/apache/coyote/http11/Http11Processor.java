package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.ResourceLoader;
import org.apache.coyote.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMITER = ": ";

    private final Socket connection;
    private final ResponseBuilder responseBuilder;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.responseBuilder = new ResponseBuilder();
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

            final String requestLine = bufferedReader.readLine();
            final String requestUri = extractRequestUri(requestLine);
            final var responseBody = ResourceLoader.get(requestUri);

            final Map<String, String> headers = new HashMap<>();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.contains(HEADER_DELIMITER)) {
                    String[] headerParts = line.split(HEADER_DELIMITER);
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            final var response = responseBuilder.build(requestUri, headers, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestUri(final String header) {
        return header.split(" ")[1];
    }
}
