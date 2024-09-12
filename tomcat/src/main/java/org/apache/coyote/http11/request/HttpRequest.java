package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.file.MimeTypeMaker;

public class HttpRequest {
    private static final String ALL_MIME_TYPE = "*/*";
    private static final String CHARSET_UTF8 = ";charset=utf-8";

    private final RequestLine requestLine;
    private final RequestHeader headers;
    private final RequestBody body;

    public HttpRequest(String inputRequestLine, String inputHeaders) {
        this.requestLine = new RequestLine(inputRequestLine);
        this.headers = new RequestHeader(inputHeaders);
        this.body = new RequestBody();
    }

    public int getContentLength() {
        return headers.getContentLength();
    }

    public void setBody(String body) {
        this.body.setBody(body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getRequestUrl() {
        return requestLine.getRequestUrl();
    }

    public String getMimeType() {
        String mimeType = headers.getMimeType();
        if (mimeType.isBlank() || mimeType.equals(ALL_MIME_TYPE)) {
            String extension = requestLine.getExtension();
            mimeType = MimeTypeMaker.getMimeTypeFromExtension(extension);
        }
        return mimeType + CHARSET_UTF8;
    }

    public Map<String, String> getUserInformation() {
        return body.getUserInformation();
    }
}
