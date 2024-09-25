package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.util.Symbol;

public class HttpResponse {

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final List<String> headers = new ArrayList<>();
    private String protocol;
    private int statusCode;
    private String statusMessage;
    private Cookies cookies = new Cookies();
    private String body;

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void addHeaders(String headerKey, String headerValue) {
        this.headers.add(buildHeaderLine(headerKey, headerValue));
    }

    private String buildHeaderLine(String key, String value) {
        return String.join(Symbol.COLON, key, value) + Symbol.SPACE;
    }

    public void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

    public void setBody(String contents) {
        int contentLength = contents.getBytes(StandardCharsets.UTF_8).length;
        this.body = contents;
        addHeaders(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public byte[] getBytes() {
        return buildResponseString().getBytes();
    }

    private String buildResponseString() {
        StringJoiner joiner = new StringJoiner(Symbol.CRLF);
        joiner.add(buildStartLine());
        if (!headers.isEmpty()) {
            joiner.add(buildHeaders());
        }
        if (cookies.hasCookies()) {
            joiner.add(buildCookies());
        }
        joiner.add(Symbol.EMPTY);
        if (body != null) {
            joiner.add(body);
        }
        return joiner.toString();
    }

    private String buildStartLine() {
        return String.join(Symbol.SPACE, protocol, String.valueOf(statusCode), statusMessage, Symbol.EMPTY);
    }

    private String buildHeaders() {
        return String.join(Symbol.CRLF, headers);
    }

    private String buildCookies() {
        return SET_COOKIE_HEADER + Symbol.COLON + cookies.getCookieLine();
    }
}
