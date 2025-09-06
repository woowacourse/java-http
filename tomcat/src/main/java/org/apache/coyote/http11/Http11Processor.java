package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String HEADER_DELIMITER = "\\s+";

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
        try (final var br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            final var outputStream = connection.getOutputStream();

            String requestLine = br.readLine();
            String[] requestParts = requestLine.split(HEADER_DELIMITER);
            String httpMethod = requestParts[0];
            String requestUri = requestParts[1];

            Map<String, String> headers = parseHttpHeaders(br);
            String body = "POST".equals(httpMethod) ? getBody(headers, br) : "";
            HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));

            if (isStaticResource(requestUri)) {
                StaticResourceProcessor.processStatic(requestUri, outputStream);
            } else {
                DynamicRequestProcessor.processDynamic(httpMethod, requestUri, body, httpCookie, outputStream);
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isStaticResource(String requestUri) {
        return requestUri.contains(".") && !requestUri.contains("/login") && !requestUri.contains("/register");
    }

    private Map<String, String> parseHttpHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerParts = line.split(":", 2);
            headers.put(headerParts[0].strip(), headerParts[1].strip());
        }
        return headers;
    }

    private String getBody(Map<String, String> headers, BufferedReader br) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);
    }
}
