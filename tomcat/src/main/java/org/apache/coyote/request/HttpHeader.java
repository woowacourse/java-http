package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;

public class HttpHeader {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;
    private final Cookies cookies;

    private HttpHeader(final Map<String, String> headers, final Cookies cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpHeader from(Map<String, String> headers) {
        final String cookie = headers.get(COOKIE_HEADER);
        return new HttpHeader(headers, Cookies.from(cookie));
    }

    public static HttpHeader readHttpHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> httpHeaderLines = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER);
            httpHeaderLines.put(header[KEY_INDEX], header[VALUE_INDEX].trim());
        }
        final String cookie = httpHeaderLines.get(COOKIE_HEADER);

        return new HttpHeader(httpHeaderLines, Cookies.from(cookie));
    }

    public Cookies getCookies() {
        return cookies;
    }

    public Optional<Cookie> getJSessionCookie() {
        return cookies.getJSessionCookie();
    }

    public String getContentLength() {
        return headers.get(CONTENT_LENGTH);
    }
}
