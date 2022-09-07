package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.HeaderField.CONTENT_TYPE;
import static org.apache.coyote.http11.HeaderField.LOCATION;

public class ResponseHeaders {

    private final String headers;

    public ResponseHeaders(String url, String body) {
        this.headers = createHeaders(url, body);
    }

    public ResponseHeaders(String redirectUrl) {
        this.headers = createHeadersForRedirect(redirectUrl);
    }

    private String createHeaders(String url, String body) {
        String contentType = getContentType(url);
        String contentLength = body.getBytes().length + " ";
        return String.join("\r\n",
                CONTENT_TYPE + contentType,
                CONTENT_LENGTH + contentLength,
                "");
    }

    private String createHeadersForRedirect(String redirectUrl) {
        return String.join("\r\n",
                LOCATION + redirectUrl,
                "");
    }

    private String getContentType(String requestUri) {
        return ContentType.find(requestUri) + ";charset=utf-8 ";
    }

    public String getHeadersToString() {
        return String.join("\r\n", headers, "");
    }

    public String getHeaders() {
        return headers;
    }
}
