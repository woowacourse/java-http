package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers;

    private ResponseHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader defaultOf(final String body) {
        final Map<String, String> headers = new LinkedHashMap<>();

        headers.put("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return new ResponseHeader(headers);
    }

    public ResponseHeader build(final String body) {
        this.headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return this;
    }

    public ResponseHeader build(
            final String body,
            final String contentType
    ) {
        this.headers.put("Content-Type", contentType + ";charset=utf-8");
        this.headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        return this;
    }

//    public static ResponseHeader build(
//            final HttpRequest httpRequest,
//            final ResponseContent responseContent
//    ) {
//        final List<String> headers = new ArrayList<>();
//
//        headers.add("Content-Type: " + ContentType.getContentType(httpRequest.getPath()) + ";charset=utf-8");
//        headers.add("Content-Length: " + responseContent.body().getBytes(StandardCharsets.UTF_8).length);
//
//        if (responseContent.location() != null) {
//            headers.add("Location: " + responseContent.location());
//        }
//
//        if (responseContent.httpCookie() != null) {
//            Map<String, String> cookies = responseContent.httpCookie().getCookies();
//            for (String name : cookies.keySet()) {
//                headers.add("Set-Cookie: " + name + "=" + cookies.get(name) + ";");
//            }
//        }
//
//        return new ResponseHeader(headers);
//    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
