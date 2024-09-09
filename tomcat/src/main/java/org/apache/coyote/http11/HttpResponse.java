package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.common.Cookies;

public class HttpResponse {

    private String protocol;
    private int statusCode;
    private String statusMessage;
    private final List<String> headers = new ArrayList<>();
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

    public void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getBytes() {
        return buildResponseString().getBytes();
    }

    private String buildResponseString() {
        return String.join("\r\n", buildStartLine(), buildHeaders(), buildCookies(), "", body);
    }

    private String buildStartLine() {
        return String.join(" ", protocol, String.valueOf(statusCode), statusMessage);
    }

    private String buildHeaderLine(String key, String value) {
        return String.join(": ", key, value) + " ";
    }

    private String buildHeaders() {
        return String.join("\r\n", headers);
    }

    private String buildCookies() {
        return "Set-Cookie: " + cookies.getCookieLine();
    }
}
