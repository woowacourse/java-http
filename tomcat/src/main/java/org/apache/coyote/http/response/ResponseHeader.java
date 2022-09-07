package org.apache.coyote.http.response;

import static org.apache.coyote.http.HttpHeaders.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpHeaders;

public class ResponseHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String NEW_LINE = "\r\n";

    private final Map<HttpHeaders, String> values;

    public ResponseHeader() {
        this.values = new LinkedHashMap<>();
    }

    public void addContentType(final String contentType) {
        values.put(CONTENT_TYPE, contentType + ";charset=utf-8");
    }

    public void addContentLength(final int contentLength) {
        values.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void addLocation(final String resource) {
        values.put(LOCATION, resource);
    }

    public void addCookie(final ResponseCookie cookie) {
        values.put(SET_COOKIE, cookie.toHeaderForm());
    }

    public String toHeaderString() {
        return values.entrySet()
                .stream()
                .map(it -> it.getKey() + HEADER_DELIMITER + it.getValue() + " ")
                .collect(Collectors.joining(NEW_LINE, "", NEW_LINE));
    }
}
