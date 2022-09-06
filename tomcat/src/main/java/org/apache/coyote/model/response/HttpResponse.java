package org.apache.coyote.model.response;

import org.apache.coyote.model.session.Cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final String body;

    private HttpResponse(ResponseLine responseLine, ResponseHeader responseHeader, String body) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public static HttpResponse of(final String extension, final String body, ResponseLine responseLine) {
        ResponseHeader responseHeader = createHeaders(extension, body);
        return new HttpResponse(responseLine, responseHeader, body);
    }

    private static ResponseHeader createHeaders(String extension, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(ResponseHeader.CONTENT_TYPE, extension);
        headers.put(ResponseHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return ResponseHeader.of(headers);
    }

    public void addCookie(Cookie cookie) {
        responseHeader.addCookie(cookie);
    }

    public String getResponse() {
        return String.join("\r\n",
                responseLine.getResponse(),
                getResponseHeaders(),
                "",
                body);
    }

    private String getResponseHeaders() {
        return responseHeader.getResponseHeaders();
    }

    public ResponseLine getResponseLine() {
        return responseLine;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public String getBody() {
        return body;
    }
}
