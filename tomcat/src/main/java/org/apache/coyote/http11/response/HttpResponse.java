package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final HttpResponseLine line;
    private final Map<String, String> header;
    private final String responseBody;

    public HttpResponse(final HttpStatus httpStatus, final String responseBody,
                        final ContentType contentType, final String redirectUrl) {
        this.line = new HttpResponseLine(httpStatus);
        this.header = new HashMap<>();
        initHeader(contentType, responseBody, redirectUrl);
        this.responseBody = responseBody;
    }

    public HttpResponse(final HttpStatus httpStatus, final String responseBody, final ContentType contentType) {
        this.line = new HttpResponseLine(httpStatus);
        this.header = new HashMap<>();
        initHeader(contentType, responseBody);
        this.responseBody = responseBody;
    }

    private void initHeader(final ContentType contentType, final String responseBody) {
        header.put("Content-Type", contentType.getContentType() + ";charset-utf-8");
        header.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    private void initHeader(final ContentType contentType, final String responseBody, final String redirectUrl) {
        initHeader(contentType, responseBody);
        header.put("Location", redirectUrl);
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                line + " ",
                "Content-Type: " + header.get("Content-Type") + " ",
                "Content-Length: " + header.get("Content-Length") + " \n",
                responseBody);
    }

    public String toFoundString() {
        return String.join("\r\n",
                line + "\r",
                "Location: " + header.get("Location") + "\r",
                "Content-Type: " + header.get("Content-Type") + "\r",
                "Content-Length: " + header.get("Content-Length") + "\r\n",
                responseBody);
    }
}
