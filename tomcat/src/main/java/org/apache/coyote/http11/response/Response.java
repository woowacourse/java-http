package org.apache.coyote.http11.response;

import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http11.Headers;

public class Response {

    private static final String BLANK_LINE_CONTENT = "";

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
        this.body = new ResponseBody(null, null);
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
        final String path = body.getPath();
        if (path == null) {
            return;
        }
        String contentType = URLConnection.guessContentTypeFromName(path);
        if (contentType == null) {
            headers.add("Content-Type", "text/html");
            return;
        }
        headers.add("Content-Type", contentType);
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
        return String.join("\r\n",
                statusLine.format(),
                headers.format(),
                BLANK_LINE_CONTENT,
                body.getContent()
        );
    }
}
