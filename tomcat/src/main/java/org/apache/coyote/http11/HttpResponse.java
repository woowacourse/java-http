package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final ContentType contentType;
    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie cookie = new HttpCookie();
    private final String body;

    public HttpResponse(HttpStatusCode statusCode, ContentType contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public String headersToString() {
        if (headers.isEmpty() && cookie.isEmpty()) {
            return null;
        }

        List<String> headerString = new ArrayList<>(headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList());
        headerString.add("Set-Cookie: " + cookie.toSetCookieString());
        return String.join("\r\n", headerString);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType.get();
    }

    public String getBody() {
        return body;
    }

    public void addCookie(String key, String value) {
        cookie.add(key, value);
    }
}
