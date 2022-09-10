package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";

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
        headers.put("Set-Cookie", "JSESSIONID=" + cookie);
        return this;
    }

    public HttpHeader contentType(final String url) {
        headers.put("Content-Type", getContentType(url).getMIMEType() + ";charset=utf-8");
        return this;
    }

    private ContentType getContentType(final String url) {
        if (url.contains(EXTENSION_DELIMITER)) {
            final String[] splitExtension = url.split("\\" + EXTENSION_DELIMITER);
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
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
            final HttpCookie httpCookie = new HttpCookie(this.headers.get("Cookie"));
            return httpCookie.containsKey("JSESSIONID");
        }
        return false;
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
    }

    public String getJSESSIONID() {
        if (hasJSESSIONID()) {
            final HttpCookie httpCookie = new HttpCookie(this.headers.get("Cookie"));
            return httpCookie.getJSESSIONID();
        }
        throw new RuntimeException("JSESSIONID가 없습니다.");
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
