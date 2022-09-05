package org.apache.coyote.model.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;
    private final String body;
    private final HttpStatusCode httpStatusCode;

    private HttpResponse(Map<String, String> headers, String body, HttpStatusCode httpStatusCode) {
        this.headers = headers;
        this.body = body;
        this.httpStatusCode = httpStatusCode;
    }

    public static HttpResponse of(final String extension, final String body, HttpStatusCode httpStatusCode) {
        Map<String, String> headers = createHeaders(extension, body);
        return new HttpResponse(headers, body, httpStatusCode);
    }

    private static Map<String, String> createHeaders(String extension, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, extension);
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return headers;
    }

    public String getResponse() {
        return String.join("\r\n",
                httpStatusCode.getResponse() +
                        CONTENT_TYPE + HEADER_DELIMITER + headers.get(CONTENT_TYPE) + ";charset=utf-8 ",
                CONTENT_LENGTH + HEADER_DELIMITER + headers.get(CONTENT_LENGTH) + " ",
                "",
                body);
    }
}
