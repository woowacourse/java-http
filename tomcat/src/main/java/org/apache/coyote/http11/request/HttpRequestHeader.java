package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.common.HttpCookie;

public class HttpRequestHeader {

    private static final String HTTP_HEADER_REGEX = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String COOKIE_HEADER_KEY = "Cookie";
    private static final String CONTENT_LENGTH_HEADER_KEY = "Content-Length";

    private final Map<String, String> value;
    private final HttpCookie httpCookie;

    public HttpRequestHeader(final Map<String, String> value) {
        this.value = value;
        this.httpCookie = getHttpCookie(value);
    }

    private HttpCookie getHttpCookie(final Map<String, String> value) {
        if (value.containsKey(COOKIE_HEADER_KEY)) {
            final String cookie = value.get(COOKIE_HEADER_KEY);
            return HttpCookie.from(cookie);
        }
        return new HttpCookie();
    }

    public static HttpRequestHeader from(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        return new HttpRequestHeader(parsingHeader(headerLines));
    }

    private static Map<String, String> parsingHeader(final List<String> headerLines) {
        final Map<String, String> headers = new HashMap<>();
        for (String headerLine : headerLines) {
            final String[] item = headerLine.split(HTTP_HEADER_REGEX);
            final String key = item[HEADER_KEY_INDEX];
            final String value = item[HEADER_VALUE_INDEX];
            headers.put(key, value);
        }
        return headers;
    }

    public String getContentLength() {
        return value.getOrDefault(CONTENT_LENGTH_HEADER_KEY, "0");
    }

    public boolean containsSession() {
        return httpCookie.containsSession();
    }

    public String getSession() {
        return httpCookie.getSession();
    }
}
