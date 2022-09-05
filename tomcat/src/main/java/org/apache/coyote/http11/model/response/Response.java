package org.apache.coyote.http11.model.response;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import java.util.ArrayList;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class Response {

    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String KEY_CONTENT_LENGTH = "Content-Length";
    public static final String ENCODE_UTF8 = ";charset=utf-8";

    private Status status;
    private Headers headers;
    private Resource resource;

    private Response(final Status status) {
        this.status = status;
        this.headers = new Headers(new ArrayList<>());
    }

    public static Response of(final Status status) {
        return new Response(status);
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

    public void addHeader(final String key, final String value) {
        this.headers.add(new Header(key, value));
    }

    public void addResource(final Resource resource) {
        this.resource = resource;
        setContentType();
        setContentLength();
    }

    public String getHeaderValue(final String key) {
        return this.headers.getValue(key);
    }

    private void setContentType() {
        this.addHeader(KEY_CONTENT_TYPE, resource.getContentType() + ENCODE_UTF8);
    }

    private void setContentLength() {
        this.addHeader(KEY_CONTENT_LENGTH, String.valueOf(resource.getContentLength()));
    }

    public Status getStatus() {
        return status;
    }
}
