package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.request.HttpMethod;

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
        ResponseHeaders headers = new ResponseHeaders();
        HttpBody responseBody = new HttpBody(body);
        headers.add(CONTENT_TYPE, contentType);
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.OK, version, headers, responseBody);
    }

    public static HttpResponse redirect(HttpVersion version, String location) {
        ResponseHeaders headers = new ResponseHeaders();
        headers.add(LOCATION, location);
        headers.add(CONTENT_LENGTH, "0");

        return new HttpResponse(HttpStatus.FOUND, version, headers, HttpBody.empty());
    }

    public static HttpResponse badRequest(HttpVersion version) {
        ResponseHeaders headers = new ResponseHeaders();
        headers.add(CONTENT_LENGTH, "0");

        return new HttpResponse(HttpStatus.BAD_REQUEST, version, headers, HttpBody.empty());
    }

    public static HttpResponse unauthorized(HttpVersion version, byte[] body) {
        ResponseHeaders headers = new ResponseHeaders();
        HttpBody responseBody = new HttpBody(body);
        headers.add(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.UNAUTHORIZED, version, headers, responseBody);
    }

    public static HttpResponse conflict(HttpVersion version, byte[] body) {
        ResponseHeaders headers = new ResponseHeaders();
        HttpBody responseBody = new HttpBody(body);
        headers.add(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.CONFLICT, version, headers, responseBody);
    }

    public static HttpResponse notFound(HttpVersion version, byte[] body) {
        ResponseHeaders headers = new ResponseHeaders();
        HttpBody responseBody = new HttpBody(body);
        headers.add(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.NOT_FOUND, version, headers, responseBody);
    }

    public static HttpResponse internalServerError(HttpVersion version, byte[] body) {
        ResponseHeaders headers = new ResponseHeaders();
        HttpBody responseBody = new HttpBody(body);
        headers.add(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.add(CONTENT_LENGTH, String.valueOf(responseBody.length()));

        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, version, headers, responseBody);
    }

    public static HttpResponse methodNotAllowed(HttpVersion version, Set<HttpMethod> allowedMethods) {
        ResponseHeaders headers = new ResponseHeaders();
        String allowedMethodNames = allowedMethods.stream()
                .map(HttpMethod::name)
                .sorted()
                .collect(Collectors.joining(", "));
        headers.add("Allow", allowedMethodNames);
        headers.add(CONTENT_LENGTH, "0");

        return new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, version, headers, HttpBody.empty());
    }

    public void addCookie(String cookie) {
        headers.add("Set-Cookie", cookie);
    }

    public byte[] serialize() {
        byte[] headerBytes = serializeHeaders().getBytes(StandardCharsets.UTF_8);
        byte[] bodyBytes = body.bytes();
        byte[] responseBytes = Arrays.copyOf(headerBytes, headerBytes.length + bodyBytes.length);
        System.arraycopy(bodyBytes, 0, responseBytes, headerBytes.length, bodyBytes.length);
        return responseBytes;
    }

    private String serializeHeaders() {
        String statusLine = version.getValue() + " " + status.code() + " " + status.reasonPhrase() + " \r\n";
        return statusLine + headers.serialize() + "\r\n";
    }
}
