package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Http11ContentTypeFinder;
import org.apache.coyote.http11.Http11Header;

public record Http11Response(Http11StatusCode statusCode, List<Http11Header> headers, List<Cookie> cookies,
                             byte[] body) {

    private static final Http11ContentTypeFinder contentTypeFinder = new Http11ContentTypeFinder();

    public static Http11Response ok(List<Http11Header> headers, List<Cookie> cookies, Path path) throws IOException {
        String contentTypes = contentTypeFinder.find(path);

        byte[] responseBody = Files.readAllBytes(path);

        List<Http11Header> copyHeader = new java.util.ArrayList<>(List.copyOf(headers));
        copyHeader.add(new Http11Header("Content-Length", responseBody.length + ""));
        copyHeader.add(new Http11Header("Content-Type", contentTypes + ";charset=utf-8"));

        return new Http11Response(Http11StatusCode.OK, copyHeader, cookies, responseBody);
    }

    public static Http11Response found(String uri) {
        List<Http11Header> headers = List.of(new Http11Header("Location", uri));
        return new Http11Response(Http11StatusCode.FOUND, headers, new ArrayList<>(), new byte[0]);
    }

    public byte[] toBytes() {
        String headers = this.headers.stream()
                .map(Http11Header::toString)
                .collect(Collectors.joining("\r\n"));
        String cookies = this.cookies.stream()
                .map(Cookie::toString)
                .collect(Collectors.joining(";"));
        String cookieHeader = "Set-Cookie: %s\r\n".formatted(cookies);
        if (cookieHeader.equals("Set-Cookie: \r\n")) {
            cookieHeader = "";
        }
        String startAndHeadersString = "HTTP/1.1 %s\r\n".formatted(statusCode)
                + "%s\r\n".formatted(headers)
                + cookieHeader
                + "\r\n";
        byte[] startLineAndHeaders = startAndHeadersString.getBytes();
        return makeResponseBytes(startLineAndHeaders);
    }

    private byte[] makeResponseBytes(byte[] startLineAndHeaders) {
        byte[] response = new byte[startLineAndHeaders.length + body.length];
        System.arraycopy(startLineAndHeaders, 0, response, 0, startLineAndHeaders.length);
        System.arraycopy(body, 0, response, startLineAndHeaders.length, body.length);
        return response;
    }
}
