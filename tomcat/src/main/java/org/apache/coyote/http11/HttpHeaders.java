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

    public HttpHeaders(List<String> headers) {
        this.headers = headers.stream()
                .map(HttpHeaders::validateAndSplit)
                .collect(Collectors.toMap(
                        header -> header[0],
                        header -> header[1]
                ));
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
}
