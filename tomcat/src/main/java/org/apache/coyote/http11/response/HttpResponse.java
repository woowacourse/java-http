package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Header;

public record HttpResponse(Http11StatusCode statusCode, List<Http11Header> headers, List<Http11Cookie> cookies,
                           byte[] body) {

    private static final Http11ContentTypeFinder contentTypeFinder = new Http11ContentTypeFinder();
    private static final String CRLF = "\r\n";

    public static HttpResponse ok(List<Http11Header> headers, List<Http11Cookie> cookies, Path path)
            throws IOException {
        String contentTypes = contentTypeFinder.find(path);

        byte[] responseBody = Files.readAllBytes(path);

        List<Http11Header> copyHeader = new ArrayList<>(List.copyOf(headers));
        copyHeader.add(new Http11Header("Content-Length", responseBody.length + ""));
        copyHeader.add(new Http11Header("Content-Type", contentTypes + ";charset=utf-8"));

        return new HttpResponse(Http11StatusCode.OK, copyHeader, cookies, responseBody);
    }

    public static HttpResponse found(String uri, Http11Cookie... cookies) {
        List<Http11Header> headers = List.of(new Http11Header("Location", uri));
        return new HttpResponse(Http11StatusCode.FOUND, headers, List.of(cookies), new byte[0]);
    }

    public boolean isHtml() {
        return headers.stream()
                .filter(header -> header.key().equals("Content-Type"))
                .map(Http11Header::value)
                .anyMatch(value -> value.contains(ContentType.HTML.getRawContentType()));
    }

    public byte[] toBytes() {
        String headers = makeHeaders();
        String cookieHeader = makeCookieHeader();
        String startAndHeadersString = "HTTP/1.1 %s".formatted(statusCode) + CRLF
                + headers + CRLF
                + cookieHeader
                + CRLF;
        byte[] startLineAndHeaders = startAndHeadersString.getBytes();
        return makeResponseBytes(startLineAndHeaders);
    }

    private String makeHeaders() {
        return this.headers.stream()
                .map(Http11Header::toString)
                .collect(Collectors.joining(CRLF));
    }

    private String makeCookieHeader() {
        String rawCookies = cookies.stream()
                .map(Http11Cookie::toString)
                .collect(Collectors.joining(";"));

        String cookieHeader = "Set-Cookie: %s".formatted(rawCookies) + CRLF;
        if (cookieHeader.equals("Set-Cookie: " + CRLF)) {
            cookieHeader = "";
        }
        return cookieHeader;
    }

    private byte[] makeResponseBytes(byte[] startLineAndHeaders) {
        byte[] response = new byte[startLineAndHeaders.length + body.length];
        System.arraycopy(startLineAndHeaders, 0, response, 0, startLineAndHeaders.length);
        System.arraycopy(body, 0, response, startLineAndHeaders.length, body.length);
        return response;
    }
}
