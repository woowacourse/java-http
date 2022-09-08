package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpHeader {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private String startLine;
    private Map<String, String> headers;

    public HttpHeader(final String requestStartLine, final String headers) {
        this.startLine = requestStartLine;
        this.headers = PairConverter.toMap(headers, "\n", ": ");
    }

    public HttpHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeader startLine(final StatusCode statusCode) {
        this.startLine = HTTP_VERSION + statusCode.getStatusMessage();
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

    public String getStartLine() {
        return startLine;
    }

    public String getMethod() {
        return getStartLine().split(" ")[0];
    }

    public String getUrl() {
        return getStartLine().split(" ")[1];
    }

    public String getHeaderByFormat() {
        StringBuilder response = new StringBuilder(startLine + "\r\n");
        for (String key : headers.keySet()) {
            response.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append(" \r\n");
        }
        return response.toString();
    }
}
