package org.apache.coyote.http11.model.response;

import java.util.ArrayList;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class Response {

    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String KEY_CONTENT_LENGTH = "Content-Length";
    public static final String ENCODE_UTF8 = ";charset=utf-8";

    private Status status;
    private Headers headers;
    private ResponseBody responseBody;

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
        return String.join("\r\n",
                "HTTP/1.1" + " " + status.getCode() + " " + status.name() + " ",
                headers.getString(),
                "",
                responseBody.getBody());
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(new Header(key, value));
    }

    public void addResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
        setContentType();
        setContentLength();
    }

    private void setContentType() {
        this.addHeader(KEY_CONTENT_TYPE, responseBody.getContentType() + ENCODE_UTF8);
    }

    private void setContentLength() {
        this.addHeader(KEY_CONTENT_LENGTH, String.valueOf(responseBody.getContentLength()));
    }
}
