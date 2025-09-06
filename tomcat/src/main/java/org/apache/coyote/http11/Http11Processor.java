package org.apache.coyote.http11;

import com.techcourse.Service;
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
    private final Service service;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.responseBuilder = new ResponseBuilder();
        this.service = new Service();
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

            byte[] responseBody;
            if (requestUri.contains("?")) {
                final Map<String, String> queryParams = extractQueryParams(requestUri);
                responseBody = service.findUser(queryParams);
            } else {
                responseBody = ResourceLoader.get(requestUri);
            }

            final Map<String, String> headers = new HashMap<>();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.contains(HEADER_DELIMITER)) {
                    String[] headerParts = line.split(HEADER_DELIMITER);
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            final var response = responseBuilder.build(requestUri, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestUri(final String header) {
        return header.split(" ")[1];
    }

    private Map<String, String> extractQueryParams(final String uri) {
        Map<String, String> queryParams = new HashMap<>();

        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");

        for (String query : queries) {
            String[] keyValues = query.split("=");
            String key = keyValues[0];
            String value = keyValues[1];
            queryParams.put(key, value);
        }

        return queryParams;
    }
}
