package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMITER = ": ";

    private final Socket connection;
    private final RequestHandler requestHandler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestHandler = new RequestHandler();
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest request = getHttpRequest(bufferedReader);
            final var response = requestHandler.handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final String requestMethod = extractRequestMethod(requestLine);
        final String requestUri = extractRequestUri(requestLine);

        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.contains(HEADER_DELIMITER)) {
                String[] headerParts = line.split(HEADER_DELIMITER);
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        return new HttpRequest(requestMethod, requestUri, headers);
    }

    private String extractRequestMethod(final String requestLine) {
        return requestLine.split(" ")[0];
    }

    private String extractRequestUri(final String header) {
        return header.split(" ")[1];
    }
}

