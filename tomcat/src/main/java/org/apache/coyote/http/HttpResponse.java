package org.apache.coyote.http;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final List<String> values;
    private String responseBody;

    private HttpResponse(final List<String> values) {
        this.values = values;
    }

    public static HttpResponse fromStatusCode(final int statusCode) {
        final List<String> values = new ArrayList<>();

        if (statusCode == 200) {
            values.add("HTTP/1.1 200 OK ");
        }
        if (statusCode == 302) {
            values.add("HTTP/1.1 302 Found ");
        }
        if (values.isEmpty()) {
            values.add("HTTP/1.1 500 Internal Server ");
        }

        return new HttpResponse(values);
    }

    public HttpResponse setContentType(final String contentType) {
        values.add("Content-Type: " + contentType + ";charset=utf-8 ");
        return this;
    }

    public HttpResponse setResponseBody(final String responseBody) {
        values.add("Content-Length: " + responseBody.getBytes().length + " ");
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse setLocation(final String path) {
        values.add("Location: " + path + " ");
        return this;
    }

    public HttpResponse setCookie(final String cookie) {
        values.add("Set-Cookie: " + cookie + " ");
        return this;
    }

    public byte[] toResponseBytes() {
        values.add("");
        return String.join("\r\n", values)
                .getBytes();
    }
}
