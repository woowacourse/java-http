package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpVersion;

public final class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private final HttpStatus status;
    private final HttpVersion version;
    private final ResponseHeaders headers;
    private final HttpBody body;

    private HttpResponse(HttpStatus status, HttpVersion version, ResponseHeaders headers, HttpBody body) {
        this.status = status;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(HttpVersion version, String contentType, byte[] body) {
        HttpBody responseBody = new HttpBody(body);
        ResponseHeaders headers = new ResponseHeaders();
        headers.add(CONTENT_TYPE, contentType);
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.OK, version, headers, responseBody);
    }

    public static HttpResponse redirect(HttpVersion version, String location) {
        ResponseHeaders headers = new ResponseHeaders();
        headers.add(LOCATION, location);
        headers.add(CONTENT_LENGTH, "0");

        return new HttpResponse(HttpStatus.FOUND, version, headers, new HttpBody(new byte[0]));
    }

    public static HttpResponse notFound(HttpVersion version, byte[] body) {
        HttpBody responseBody = new HttpBody(body);
        ResponseHeaders headers = new ResponseHeaders();
        headers.add(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.NOT_FOUND, version, headers, responseBody);
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(serializeHeaders().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body.bytes());
    }

    @Override
    public String toString() {
        return serializeHeaders() + body.asString(StandardCharsets.UTF_8);
    }

    private String serializeHeaders() {
        String statusLine = version.getValue() + " " + status.code() + " " + status.reasonPhrase() + " \r\n";
        return statusLine + headers.serialize() + "\r\n";
    }
}
