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

            log.info("{} {} | message: {} {}", request.method(), request.uri(), request.headers(), request.body());

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
        final Map<String, String> queryParams = extractQueryParams(requestUri);

        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            if (line.contains(HEADER_DELIMITER)) {
                String[] headerParts = line.split(HEADER_DELIMITER);
                headers.put(headerParts[0], headerParts[1]);
            }
            line = bufferedReader.readLine();
        }

        String contentLength = headers.get("Content-Length");

        if (contentLength == null) {
            return new HttpRequest(requestMethod, requestUri, headers, queryParams, null);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        int length = Integer.parseInt(contentLength);
        char[] bodyChars = new char[length];
        int read = bufferedReader.read(bodyChars, 0, length);
        bodyBuilder.append(bodyChars, 0, read);

        return new HttpRequest(requestMethod, requestUri, headers, queryParams, bodyBuilder.toString());
    }

    private String extractRequestMethod(final String requestLine) {
        return requestLine.split(" ")[0];
    }

    private String extractRequestUri(final String header) {
        return header.split(" ")[1];
    }

    private Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) return null;

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

