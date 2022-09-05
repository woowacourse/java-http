package org.apache.coyote.http11.model.response;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class HttpResponse {

    public static final String ENCODE_UTF8 = ";charset=utf-8";

    private Status status;
    private Headers headers;
    private Resource resource;

    private HttpResponse(final Status status) {
        this.status = status;
        this.headers = Headers.empty();
    }

    public static HttpResponse of(final Status status) {
        return new HttpResponse(status);
    }

    public boolean hasLocation() {
        return headers.hasHeader(Header.LOCATION);
    }

    public byte[] getBytes() {
        return getString().getBytes();
    }

    private String getString() {
        String defaultFormat = String.join(CRLF,
                "HTTP/1.1" + " " + status.getCode() + " " + status.name() + " ",
                headers.getString());
        if (resource != null) {
            return joinBody(defaultFormat);
        }
        return defaultFormat;
    }

    private String joinBody(final String defaultFormat) {
        return String.join(CRLF,
                defaultFormat,
                "",
                resource.getBody());
    }

    public void addHeader(final Header header, final String value) {
        this.headers.add(header.getKey(), value);
    }
    
    public void addResource(final Resource resource) {
        this.resource = resource;
        setContentType();
        setContentLength();
    }

    public String getHeaderValue(final Header header) {
        return headers.getValue(header.getKey());
    }

    private void setContentType() {
        this.addHeader(Header.CONTENT_TYPE, resource.getContentType() + ENCODE_UTF8);
    }

    private void setContentLength() {
        this.addHeader(Header.CONTENT_LENGTH, String.valueOf(resource.getContentLength()));
    }

    public Status getStatus() {
        return status;
    }
}
