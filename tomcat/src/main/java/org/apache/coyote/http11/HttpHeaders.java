package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHeaders {

    private static final Logger log = LoggerFactory.getLogger(HttpHeaders.class);
    private static final String DELIMITER = ": ";

    private final Map<String, String> headers;
    private final HttpCookies cookies;

    public HttpHeaders(List<String> headers) {
        this.headers = headers.stream()
                .filter(header -> !header.startsWith("Cookie: "))
                .map(HttpHeaders::validateAndSplit)
                .collect(Collectors.toMap(
                        header -> header[0],
                        header -> header[1]
                ));
        this.cookies = HttpCookies.parse(
                headers.stream()
                        .filter(header -> header.startsWith("Cookie: "))
                        .findAny()
                        .orElse("")
        );
    }

    private static String[] validateAndSplit(String header) {
        String[] keyAndValue = header.split(DELIMITER);
        if (keyAndValue.length == 2) {
            return keyAndValue;
        }
        RuntimeException exception = new IllegalArgumentException("Invalid header: " + header);
        log.error(exception.getMessage(), exception);
        throw exception;
    }

    public String get(HttpHeader header) {
        String value = headers.get(header.getValue());
        if (value == null) {
            throw new IllegalArgumentException("Header " + header.getValue() + " not found");
        }
        return value;
    }

    public boolean containsSession() {
        return cookies.containsSession();
    }
}
