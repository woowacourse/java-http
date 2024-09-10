package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.Header;

public class RequestHeaders {

    private static final String COOKIE_FIELD_DELIMITER = ": ";

    private final Map<String, String> values;

    public RequestHeaders(String[] headers) {
        this.values = Arrays.stream(headers)
                .filter(header -> header.contains(COOKIE_FIELD_DELIMITER))
                .map(header -> header.split(COOKIE_FIELD_DELIMITER, 2))
                .filter(token -> token.length == 2)
                .collect(Collectors.toMap(token -> token[0].trim(), token -> token[1].trim()));
    }

    public String getCookies() {
        return values.getOrDefault(Header.COOKIE.value(), "");
    }

    public int getContentLength() {
        String contentLength = values.getOrDefault(Header.CONTENT_LENGTH.value(), "0");
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
               "values=" + values +
               '}';
    }
}
