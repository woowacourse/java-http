package org.apache.catalina.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.http.ContentType;

public class RequestHeader {
    public static final String ACCEPT = "Accept";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    public static final String COOKIE_SEPARATOR = ";";

    private final Map<String, String> headers;
    private final ContentType contentType;

    public RequestHeader(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
        this.contentType = ContentType.of(headers.get(ACCEPT));
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public HttpCookie getCookie() {
        String setCookies = headers.get(COOKIE);
        if (setCookies == null) {
            return new HttpCookie(new HashMap<>());
        }
        Map<String, String> cookie = Arrays.stream(setCookies.split(COOKIE_SEPARATOR))
                .map(param -> param.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));

        return new HttpCookie(cookie);
    }
}
