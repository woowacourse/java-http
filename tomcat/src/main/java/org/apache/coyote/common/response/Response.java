package org.apache.coyote.common.response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.header.Cookie;
import org.apache.coyote.common.header.Header;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;

public class Response {

    private static final String RESPONSE_MESSAGE_DELIMITER = "\r\n";
    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final String HEADER_BODY_DELIMITER = "";
    private static final String HEADER_VALUE_DELIMITER = ";";

    private final HttpVersion httpVersion;
    private Status status;
    private final Map<String, String> headers;
    private String body;

    public static ResponseBuilder builder(final HttpVersion httpVersion) {
        return new ResponseBuilder(httpVersion);
    }

    private Response(final HttpVersion httpVersion, final Status status, final Map<String, String> headers, final String body) {
        this.httpVersion = httpVersion;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        return String.join(RESPONSE_MESSAGE_DELIMITER,
                makeStartLine(),
                combineHeaders(),
                body);
    }

    private String makeStartLine() {
        return String.join(" ", httpVersion.getValue(), status.getValue());
    }

    private String combineHeaders() {
        return headers.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + HEADER_KEY_VALUE_DELIMITER + entry.getValue() + " ")
                .collect(Collectors.joining(RESPONSE_MESSAGE_DELIMITER, "",
                        HEADER_BODY_DELIMITER + RESPONSE_MESSAGE_DELIMITER));
    }


    public Response setContentType(final MediaType mediaType, final Charset charset) {
        return setHeader(Header.CONTENT_TYPE,
                combineHeaderValues(mediaType.getValue(), "charset=" + charset.getValue()));
    }

    private String combineHeaderValues(final String... values) {
        return String.join(HEADER_VALUE_DELIMITER, values);
    }

    public Response setLocation(final String url) {
        return setHeader(Header.LOCATION, url);
    }

    public Response setContentType(final MediaType mediaType) {
        return setContentType(mediaType, Charset.UTF8);
    }

    public Response setStatus(final Status status) {
        this.status = status;
        return this;
    }

    public Response setContentLength(final int length) {
        return setHeader(Header.CONTENT_LENGTH, length + "");
    }

    public Response setHeader(final Header header, final String headerValue) {
        headers.put(header.getValue(), headerValue);
        return this;
    }

    public Response setBody(final String body) {
        this.body = body;
        return this;
    }

    public static class ResponseBuilder {

        private final HttpVersion httpVersion;
        private final Status status;
        private final Map<String, String> headers;
        private String body;

        ResponseBuilder(final HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            this.status = Status.OK;
            this.headers = new HashMap<>();
            headers.put(Header.CONTENT_TYPE.getValue(), MediaType.TEXT_HTML.getValue());
            this.body = "";
        }

        public Response build() {
            return new Response(httpVersion, status, this.headers, this.body);
        }

        public ResponseBuilder setJSessionIdCookie(final Cookie cookie) {
            if (cookie.getValue(Cookie.SESSION_ID_COOKIE_KEY).isEmpty()) {
//                final String cookieValue = Cookie.setCookieBuilder(Cookie.SESSION_ID_COOKIE_KEY,
//                                UUID.randomUUID().toString())
//                                .asString();
//                System.out.println(cookieValue);
                headers.put(Header.SET_COOKIE.getValue(), cookie.generateSessionIdCookie());
            }
            return this;
        }
    }
}
