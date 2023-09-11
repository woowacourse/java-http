package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.header.HeaderKey;
import org.apache.coyote.http11.request.header.Headers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Objects.nonNull;

public class HttpResponse {

    private static final String HEADER_BODY_SEPARATOR = "";

    private StatusLine statusLine;
    private Headers headers;
    private String body;

    public HttpResponse() {
        headers = new Headers(new LinkedHashMap<>());
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public void addHeader(final HeaderKey key, final String value) {
        headers.addHeader(key, value);
    }

    public byte[] getBytes() {
        final List<String> result = new ArrayList<>();

        result.add(statusLine.get());
        result.addAll(headers.toList());
        result.add(HEADER_BODY_SEPARATOR);

        if (nonNull(body)) {
            result.add(body);
        }

        return String.join("\r\n", result).getBytes();
    }

    public String get(final String body) throws IOException {
        final List<String> responses = new ArrayList<>();
        responses.add("HTTP/1.1 200 OK ");
        responses.add(contentLength(body));

        responses.add(HEADER_BODY_SEPARATOR);
        responses.add(body);

        return String.join("\r\n", responses);
    }

    private String contentLength(final String body) {
        return "Content-Length: " + body.getBytes().length + " ";
    }
}
