package org.apache.catalina.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.http.ContentType;
import org.apache.catalina.parser.HttpRequestParser;

public class RequestHeader {

    private static final String ACCEPT = "Accept";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String COOKIE_SEPARATOR = ";";
    private static final int DEFAULT_CONTENT_LENGTH = 0;

    private final Map<String, String> headers;
    private final ContentType contentType;
    private final HttpCookie httpCookie;

    public RequestHeader() {
        this.headers = new HashMap<>();
        this.contentType = ContentType.of(null);
        this.httpCookie = new HttpCookie();
    }

    public RequestHeader(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
        this.contentType = ContentType.of(headers.get(ACCEPT));
        this.httpCookie = findCookie();
    }

    private HttpCookie findCookie() {
        String setCookies = headers.get(COOKIE);
        if (setCookies == null) {
            return new HttpCookie();
        }

        List<String> params = List.of(setCookies.split(COOKIE_SEPARATOR));
        Map<String, String> cookie = HttpRequestParser.parseParamValues(params);
        return new HttpCookie(cookie);
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return DEFAULT_CONTENT_LENGTH;
        }
        return Integer.parseInt(contentLength);
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}
