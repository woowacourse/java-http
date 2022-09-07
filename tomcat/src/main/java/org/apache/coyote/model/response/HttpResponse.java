package org.apache.coyote.model.response;

import org.apache.coyote.model.session.Cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final String body;

    private HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader, final String body) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public static HttpResponse of(final String extension, final String body, final ResponseLine responseLine) {
        final ResponseHeader responseHeader = createHeaders(extension, body);
        return new HttpResponse(responseLine, responseHeader, body);
    }

    private static ResponseHeader createHeaders(final String extension, final String body) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(ResponseHeader.CONTENT_TYPE, extension);
        headers.put(ResponseHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return ResponseHeader.of(headers);
    }

    public void addCookie(final Cookie cookie) {
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

    public String getBody() {
        return body;
    }
}
