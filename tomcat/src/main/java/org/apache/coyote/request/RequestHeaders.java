package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.cookie.HttpCookies;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpHeader;

public class RequestHeaders {

    private static final int HEADER_LINE_ELEMENT_COUNT = 2;
    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()).length() > 0) {
            addValidHeader(headers, line);
        }
        return new RequestHeaders(headers);
    }

    private static void addValidHeader(Map<String, String> headers, String line) {
        final var elements = line.split(HttpHeader.DELIMITER);
        if (elements.length != HEADER_LINE_ELEMENT_COUNT) {
            throw HttpException.ofBadRequest();
        }
        final var key = elements[0];
        final var value = elements[1].trim();
        headers.put(key, value);
    }

    public int getContentLength() {
        String contentLengthHeader = HttpHeader.CONTENT_LENGTH.getValue();
        if (!headers.containsKey(contentLengthHeader)) {
            return 0;
        }
        return Integer.parseInt(headers.get(contentLengthHeader));
    }

    public boolean hasParametersAsBody() {
        return CONTENT_TYPE_URL_ENCODED.equals(headers.get(HttpHeader.CONTENT_TYPE.getValue()));
    }

    public HttpCookies getCookies() {
        String cookieHeader = HttpHeader.COOKIE.getValue();
        if (!headers.containsKey(cookieHeader)) {
            return new HttpCookies(new HashMap<>());
        }
        String cookies = headers.get(cookieHeader);
        return HttpCookies.ofRequestHeader(cookies);
    }
}
