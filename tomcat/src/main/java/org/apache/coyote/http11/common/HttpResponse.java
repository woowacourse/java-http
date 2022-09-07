package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.http11.common.HttpHeaders.SET_COOKIE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.constant.MediaType;

public class HttpResponse {

    private static final String BLANK = " ";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    private String body;
    private HttpStatus status;
    private final HttpHeaders headers;

    public HttpResponse() {
        this.body = "";
        this.status = HttpStatus.OK;
        this.headers = new HttpHeaders(new LinkedHashMap<>());
    }

    public void setBody(final String body) {
        this.body = body;
        final byte[] responseBody = body.getBytes();

        headers.addHeader(CONTENT_TYPE, MediaType.TEXT_HTML.getValue() + CHARSET_UTF_8);
        headers.addHeader(CONTENT_LENGTH, String.valueOf(responseBody.length));
    }

    public void setBody(final Path path) throws IOException {
        final byte[] responseBody = Files.readAllBytes(path);

        headers.addHeader(CONTENT_TYPE, Files.probeContentType(path) + CHARSET_UTF_8);
        headers.addHeader(CONTENT_LENGTH, String.valueOf(responseBody.length));
        this.body = new String(responseBody);
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setLocation(final String location) {
        headers.addHeader("Location", location);
    }

    public void addHeader(final String key, final String value) {
        headers.addHeader(key, value);
    }

    public void addSetCookie(final String key, final String value) {
        headers.addCookie(key, value);
    }

    public byte[] toBytes() {
        final String response = toString();

        return response.getBytes();
    }

    @Override
    public String toString() {
        if (headers.hasCookie()) {
            addHeader(SET_COOKIE, headers.getAllCookie());
        }

        final String statusLine = HTTP_VERSION + status.getStatusCode() + BLANK + status.getStatusMessage();
        return String.join("\r\n",
                statusLine,
                headers.toString(),
                "",
                body);
    }
}
