package org.apache.coyote.http11.response;

import java.util.EnumMap;

import static org.apache.coyote.http11.response.Header.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.Header.CONTENT_TYPE;
import static org.apache.coyote.http11.response.Header.SET_COOKIE;
import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

public class HttpResponse {

    private static final String SPACE_CRLF = " \r\n";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";
    private static final String COLON = ": ";
    private static final String HTTP11 = "HTTP/1.1";

    private final EnumMap<Header, String> headers = new EnumMap<>(Header.class);
    private String protocol;
    private StatusCode statusCode;
    private String body;

    public HttpResponse() {
        protocol = HTTP11;
        statusCode = OK;
        body = "";
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(SPACE).append(statusCode.format()).append(SPACE_CRLF);
        headers.forEach((header, value) -> sb.append(header.getName()).append(COLON).append(value).append(SPACE_CRLF));
        sb.append(CRLF).append(body);

        return sb.toString();
    }

    public HttpResponse addBody(String body) {
        this.body = body;
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return this;
    }

    public HttpResponse addBaseHeader(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
        return this;
    }

    public HttpResponse addHeader(Header header, String value) {
        headers.put(header, value);
        return this;
    }

    public HttpResponse redirect(String url) {
        statusCode = FOUND;
        headers.put(Header.LOCATION, url);
        return this;
    }

    public HttpResponse setCookie(String sessionId) {
        headers.put(SET_COOKIE, "JSESSIONID=" + sessionId);
        return this;
    }

    public HttpResponse setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
