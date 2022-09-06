package org.apache.coyote.http11.model.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.model.session.Cookie;

public class HttpResponse {

    private static final String HEADER_DELIMITER = ": ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";

    private final ResponseLine responseLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final ResponseLine responseLine, final Map<String, String> headers,
                         final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final ResponseStatusCode statusCode, final String version,
                                  final ContentType contentType, final String body) {
        Map<String, String> headers = initHeaders(contentType, body);
        return new HttpResponse(ResponseLine.of(statusCode, version), headers, body);
    }

    public static HttpResponse of(final ResponseStatusCode statusCode, final String version,
                                  final ContentType contentType) {
        Map<String, String> headers = initHeaders(contentType, "");
        return new HttpResponse(ResponseLine.of(statusCode, version), headers, "");
    }

    private static Map<String, String> initHeaders(final ContentType contentType, final String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, contentType.getType() + ";charset=utf-8 ");
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return headers;
    }

    public void addCookie(Cookie cookie) {
        headers.put(SET_COOKIE, cookie.getCookieToString());
    }

    public void addLocationHeader(final String location) {
        headers.put(LOCATION, location);
    }

    public String getResponse() {
        return String.join("\r\n",
                responseLine.getResponseLine(),
                getHeadersToString(),
                "",
                this.body);
    }

    private String getHeadersToString() {
        return headers.keySet()
                .stream()
                .map(key -> key + HEADER_DELIMITER + headers.get(key))
                .collect(Collectors.joining(" \r\n"));
    }
}
