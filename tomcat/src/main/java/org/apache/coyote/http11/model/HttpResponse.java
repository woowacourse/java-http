package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HEADER_DELIMITER = ": ";
    private static final String RESPONSE_LINE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final Map<String, String> headers, final String body) {
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final ContentType contentType, final String body) {
        Map<String, String> headers = initHeaders(contentType, body);
        return new HttpResponse(headers, body);
    }

    private static Map<String, String> initHeaders(final ContentType contentType, final String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, contentType.getType());
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return headers;
    }

    public String getResponse() {
        return String.join("\r\n",
                RESPONSE_LINE,
                CONTENT_TYPE + HEADER_DELIMITER + headers.get(CONTENT_TYPE) + ";charset=utf-8 ",
                CONTENT_LENGTH + HEADER_DELIMITER + headers.get(CONTENT_LENGTH) + " ",
                "",
                this.body);
    }
}
