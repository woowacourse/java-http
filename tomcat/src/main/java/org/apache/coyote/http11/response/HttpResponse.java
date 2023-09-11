package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SP = " ";
    private static final String HEADER_DELIMITER = ": ";

    private StatusCode statusCode;
    private ContentType contentType;
    private String responseBody;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse() {
    }

    public byte[] toBytes() {
        String responseHeader = String.join(CRLF,
                HTTP_VERSION + statusCode.getValue() + SP,
                CONTENT_TYPE + HEADER_DELIMITER + contentType.getValue() + SP,
                CONTENT_LENGTH + HEADER_DELIMITER + responseBody.getBytes().length + SP);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerInfo = entry.getKey() + HEADER_DELIMITER + entry.getValue() + SP;
            responseHeader = String.join(CRLF, responseHeader, headerInfo);
        }

        return String.join(CRLF, responseHeader, "", responseBody).getBytes();
    }

    private static final String LOCATION = "Location";
    private static final String JSESSIONID = "JSESSIONID=";

    public HttpResponse statusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse contentType(final ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse responseBody(final String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse redirect(final String redirectUrl) {
        headers.put(LOCATION, redirectUrl);
        return this;
    }

    public HttpResponse addCookie(String sessionId) {
        headers.put(SET_COOKIE, JSESSIONID + sessionId);
        return this;
    }
}
