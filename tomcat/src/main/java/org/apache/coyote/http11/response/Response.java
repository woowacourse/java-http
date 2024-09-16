package org.apache.coyote.http11.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.coyote.http11.common.Headers;

public class Response {

    private static final String BLANK_LINE_CONTENT = "";
    private static final String ENCODING_OPTION = ";charset=utf-8";

    private StatusLine statusLine;
    private Headers headers;
    private ResponseBody body;

    public Response(final StatusLine statusLine, final Headers headers, final ResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public Response() {
        this.statusLine = new StatusLine(null, null, null);
        this.headers = new Headers(new ConcurrentHashMap<>());
        this.body = new ResponseBody(null);
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setBody(final ResponseBody body) {
        this.body = body;
    }

    public void addHeader(final String field, final String value) {
        headers.add(field, value);
    }

    public void addLocation(final String location) {
        headers.add("Location", "/" + location);
    }

    public void addContentType() {
        if (body == null) {
            return;
        }
        final String content = body.getContent();
        if (content == null || content.isBlank()) {
            return;
        }
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            final String contentType = guessContentType(inputStream);
            headers.add("Content-Type", contentType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String guessContentType(final InputStream inputStream) throws IOException {
        String contentType = URLConnection.guessContentTypeFromStream(inputStream);
        if (contentType == null) {
            contentType = "text/html";
        }
        if (Headers.isTextType(contentType)) {
            return contentType + ENCODING_OPTION;
        }
        return contentType;
    }

    public void addContentLength() {
        if (body == null) {
            return;
        }
        final String resource = body.getContent();
        if (resource == null) {
            return;
        }
        final int contentLength = resource.getBytes().length;
        headers.add("Content-Length", String.valueOf(contentLength));
    }

    public ResponseBody getBody() {
        return body;
    }

    public String format() {
        return Stream.of(
                        statusLine.format(),
                        headers.format(),
                        BLANK_LINE_CONTENT,
                        body.getContent()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\r\n"));
    }
}
