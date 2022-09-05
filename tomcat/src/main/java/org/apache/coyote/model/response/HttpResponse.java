package org.apache.coyote.model.response;

import org.apache.coyote.model.ContentType;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_HTTP_RESPONSE_VALUE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(Map<String, String> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final String extension, final String body) {
        Map<String, String> headers = createHeaders(extension, body);
        return new HttpResponse(headers, body);
    }

    private static Map<String, String> createHeaders(String extension, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, extension);
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return headers;
    }

    public String getOkResponse() {
        return String.join("\r\n",
                DEFAULT_HTTP_RESPONSE_VALUE,
                CONTENT_TYPE + HEADER_DELIMITER + headers.get(CONTENT_TYPE) + ";charset=utf-8 ",
                CONTENT_LENGTH + HEADER_DELIMITER + headers.get(CONTENT_LENGTH) + " ",
                "",
                body);
    }
}
