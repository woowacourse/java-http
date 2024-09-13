package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.HttpHeader;

public class HttpResponse {

    private static final String TYPE_OF_HTML = "text/html";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.headers = new ResponseHeaders();
        this.body = null;
    }

    public void sendRedirect(String location) {
        statusLine.setHttpStatusCode(HttpStatusCode.FOUND);
        headers.put(HttpHeader.LOCATION, location);
    }

    public boolean hasLocation() {
        return headers.has(HttpHeader.LOCATION);
    }

    public void setCookie(String cookie) {
        headers.put(HttpHeader.SET_COOKIE, cookie);
    }

    public void setContentType(String contentType) {
        HttpHeader contentTypeHeader = HttpHeader.CONTENT_TYPE;

        if (TYPE_OF_HTML.equals(contentType)) {
            headers.put(contentTypeHeader, contentType + CHARSET_UTF_8);
            return;
        }
        headers.put(contentTypeHeader, contentType);
    }

    public void setContentLength(int contentLength) {
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLocation() {
        return headers.get(HttpHeader.LOCATION);
    }

    public String getResponse() {
        return String.join("\r\n",
                String.format("%s", statusLine.getMessage()),
                String.format("%s", headers.getMessage()),
                "",
                body);
    }
}
