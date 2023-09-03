package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final String redirectUrl;

    public HttpResponse(final StatusCode statusCode,
                        final ContentType contentType,
                        final String responseBody,
                        final String redirectUrl) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.redirectUrl = redirectUrl;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
    private void initHeader(final ContentType contentType, final String responseBody) {
        header.put("Content-Type", contentType.getContentType() + ";charset-utf-8");
        header.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    private void initHeader(final ContentType contentType, final String responseBody, final String redirectUrl) {
        initHeader(contentType, responseBody);
        header.put("Location", redirectUrl);
    }
}
