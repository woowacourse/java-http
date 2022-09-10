package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpHeader {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";

    private Map<String, String> headers;

    public HttpHeader(final String headers) {
        this.headers = PairConverter.toMap(headers, "\n", ": ");
    }

    public HttpHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeader startLine(final StatusCode statusCode) {
        headers.put(HTTP_VERSION, statusCode.getStatusMessage());
        return this;
    }

    public HttpHeader cookie(final String cookie) {
        headers.put("Set-Cookie", JSESSIONID + "=" + cookie);
        return this;
    }

    public HttpHeader contentType(final String url) {
        headers.put("Content-Type", getContentType(url).getMIMEType() + ";charset=utf-8");
        return this;
    }

    private ContentType getContentType(final String url) {
        return ContentType.getContentType(url);
    }

    public HttpHeader contentLength(final int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));
        return this;
    }

    public HttpHeader location(final String url) {
        headers.put("Location", url);
        return this;
    }

    public boolean hasJSESSIONID() {
        if (hasCookie()) {
            final HttpCookie httpCookie = new HttpCookie(this.headers.get(COOKIE));
            return httpCookie.containsKey(JSESSIONID);
        }
        return false;
    }

    public boolean hasCookie() {
        return headers.containsKey(COOKIE);
    }

    public String getJSESSIONID() {
        if (hasJSESSIONID()) {
            final HttpCookie httpCookie = new HttpCookie(this.headers.get(COOKIE));
            return httpCookie.getJSESSIONID(JSESSIONID);
        }
        throw new RuntimeException(JSESSIONID + "가 없습니다.");
    }

    public String getHeaderByFormat() {
        StringBuilder response = new StringBuilder();

        getRequestLineFormat(response);
        for (String key : headers.keySet()) {
            if (key.equals(HTTP_VERSION)) {
                continue;
            }
            response.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append(" \r\n");
        }
        return response.toString();
    }

    private void getRequestLineFormat(final StringBuilder response) {
        response.append("HTTP/1.1")
                .append(headers.get("HTTP/1.1"))
                .append(" \r\n");
    }
}
