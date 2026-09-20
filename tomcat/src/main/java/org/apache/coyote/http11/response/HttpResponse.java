package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.headers.ResponseHeaders;
import org.apache.coyote.http11.response.line.HttpStatus;
import org.apache.coyote.http11.response.line.StatusLine;
import org.apache.coyote.http11.session.HttpCookie;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String CHARSET = ";charset=utf-8";

    private final StatusLine statusLine;
    private final ResponseHeaders headers = new ResponseHeaders();
    private final String body;

    private HttpResponse(HttpStatus httpStatus, String body) {
        this.statusLine = StatusLine.from(httpStatus);
        this.body = body;
    }

    public static HttpResponse ok(ContentType contentType, String body) {
        HttpResponse response = new HttpResponse(HttpStatus.OK, body);
        response.addContentType(contentType);
        response.addContentLength();
        return response;
    }

    public static HttpResponse notFound(String body) {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND, body);
        response.addContentType(ContentType.HTML);
        response.addContentLength();
        return response;
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, "");
        response.headers.add("Location", location);
        response.addContentLength();
        return response;
    }

    public static HttpResponse redirect(String location, HttpCookie cookie) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, "");
        response.headers.add("Location", location);
        response.headers.add("Set-Cookie", cookie.serialize());
        response.addContentLength();
        return response;
    }

    private void addContentType(ContentType contentType) {
        headers.add("Content-Type", contentType.getValue() + CHARSET);
    }

    private void addContentLength() {
        headers.add("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(serialize().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String serialize() {
        List<String> lines = new ArrayList<>();
        lines.add(statusLine.serialize());
        lines.addAll(headers.toLines());
        lines.add("");
        if (!body.isEmpty()) {
            lines.add(body);
        }
        return String.join(DELIMITER, lines);
    }

}
