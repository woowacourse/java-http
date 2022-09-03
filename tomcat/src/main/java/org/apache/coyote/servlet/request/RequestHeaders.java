package org.apache.coyote.servlet.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_LINE_ELEMENT_COUNT = 2;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : headerLines) {
            addValidHeader(headers, line);
        }
        return new RequestHeaders(headers);
    }

    private static void addValidHeader(Map<String, String> headers, String line) {
        final var elements = line.split(HEADER_DELIMITER);
        if (elements.length != HEADER_LINE_ELEMENT_COUNT) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        final var key = elements[0];
        final var value = elements[1];
        headers.put(key, value);
    }

    public int getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public boolean hasParametersAsBody() {
        return CONTENT_TYPE_URL_ENCODED.equals(headers.get(CONTENT_TYPE));
    }
}
