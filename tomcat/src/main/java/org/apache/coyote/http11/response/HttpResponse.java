package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.Http11Header.CONTENT_LENGTH;
import static org.apache.coyote.http11.Http11Header.CONTENT_TYPE;
import static org.apache.coyote.http11.Http11Header.LOCATION;
import static org.apache.coyote.http11.Http11Header.SET_COOKIE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Header;

public final class HttpResponse {

    private static final Http11ContentTypeFinder contentTypeFinder = new Http11ContentTypeFinder();

    private static final String CRLF = "\r\n";

    private Http11StatusCode statusCode;
    private final List<Http11Header> headers;
    private final List<Http11Cookie> cookies;
    private byte[] body;

    public HttpResponse() {
        headers = new ArrayList<>();
        cookies = new ArrayList<>();
        statusCode = Http11StatusCode.OK;
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

        String cookieHeader = (SET_COOKIE + " %s").formatted(rawCookies) + CRLF;
        if (cookieHeader.equals(SET_COOKIE + " " + CRLF)) {
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

    public void addCookie(Http11Cookie cookie) {
        cookies.add(cookie);
    }

    public void setRedirect(String redirectUri) {
        addHeader(new Http11Header(LOCATION, redirectUri));
        setStatusCode(Http11StatusCode.FOUND);
        body = new byte[0];
    }

    private void addHeader(Http11Header header) {
        headers.add(header);
    }

    public void setBodyAndContentType(Path path) {
        this.body = readStaticData(path);
        addHeader(new Http11Header(CONTENT_LENGTH, body.length + ""));
        addHeader(new Http11Header(CONTENT_TYPE, contentTypeFinder.find(path) + ";charset=utf-8"));
    }

    private byte[] readStaticData(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public void setStatusCode(Http11StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Http11StatusCode getStatusCode() {
        return statusCode;
    }

    public Optional<Http11Header> findHeader(String key) {
        return headers.stream()
                .filter(header -> header.key().equals(key))
                .findFirst();
    }
}
