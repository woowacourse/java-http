package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SP = " ";
    private static final String HEADER_DELIMITER = ": ";

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final Map<String, String> headers;

    private HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody, final Map<String, String> headers) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.headers = headers;
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

    public static HttpResponse toNotFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, ContentType.TEXT_HTML, ViewLoader.toNotFound(), null);
    }

    public static HttpResponse toUnauthorized() {
        return new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML, ViewLoader.toUnauthorized(), null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private static final String LOCATION = "Location";
        private static final String JSESSIONID = "JSESSIONID=";
        private StatusCode statusCode = StatusCode.OK;
        private ContentType contentType = ContentType.WILD_CARD;
        private String responseBody = "";
        private Map<String, String> headers = new HashMap<>();

        private Builder() {
        }

        public Builder statusCode(final StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder contentType(final ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder responseBody(final String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder redirect(final String redirectUrl) {
            headers.put(LOCATION, redirectUrl);
            return this;
        }

        public Builder addCookie(String sessionId) {
            headers.put(SET_COOKIE, JSESSIONID + sessionId);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, contentType, responseBody, headers);
        }
    }
}
