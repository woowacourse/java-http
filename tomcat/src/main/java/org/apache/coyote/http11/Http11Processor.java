package org.apache.coyote.http11;

import com.techcourse.Service;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpRequest;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest request = getHttpRequest(bufferedReader);

            byte[] responseBody;
            if (request.uri().contains("?")) {
                final Map<String, String> queryParams = extractQueryParams(request.uri());
                responseBody = service.findUser(queryParams);
            } else {
                responseBody = ResourceLoader.get(request.uri());
            }

            final var response = responseBuilder.build(request.uri(), "200 OK", responseBody, null);

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
