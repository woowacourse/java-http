package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.file.MimeTypeMaker;

import com.techcourse.model.User;

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

    public String getSession() {
        return headers.getSession();
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
            String extension = getExtension();
            mimeType = MimeTypeMaker.getMimeTypeFromExtension(extension);
        }
        return mimeType + CHARSET_UTF8;
    }

    private String getExtension() {
        return requestLine.getExtension();
    }

    public User getUser() {
        return body.getUser();
    }

    public Map<String, String> getUserInformation() {
        return body.getUserInformation();
    }
}
