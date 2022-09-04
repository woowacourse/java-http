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

    private final ResponseLine responseLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final ResponseLine responseLine, final Map<String, String> headers,
                         final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final ResponseLine responseLine, final ContentType contentType,
                                  final String body) {
        Map<String, String> headers = initHeaders(contentType, body);
        return new HttpResponse(responseLine, headers, body);
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

    public void addHeader(String key, String value) {
        headers.put(key, value);
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
                .map(key -> key + HEADER_DELIMITER + headers.get(key) + " ")
                .collect(Collectors.joining());
    }
}
