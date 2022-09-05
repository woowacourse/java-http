package org.apache.coyote.http11;

import java.io.File;
import java.util.Optional;
import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.utils.FileUtil;
import org.apache.coyote.http11.utils.UuidUtil;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final String body;

    public static HttpResponse of(final HttpRequest httpRequest, final HttpStatusCode statusCode, final String url) {
        final File file = FileUtil.findFile(url);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(httpRequest, statusCode, contentType, responseBody);
    }

    public HttpResponse(final HttpRequest httpRequest, final HttpStatusCode statusCode, final String contentType,
                        final String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = initHeaders(httpRequest, contentType, body);
    }

    private HttpHeaders initHeaders(final HttpRequest httpRequest, final String contentType, final String body) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addValue("Content-Type", contentType + ";charset=utf-8");
        httpHeaders.addValue("Content-Length", body.getBytes().length);

        addCookie(httpRequest, httpHeaders);
        return httpHeaders;
    }

    private void addCookie(final HttpRequest httpRequest, final HttpHeaders httpHeaders) {
        Optional<String> jSessionId = httpRequest.getHeaders()
                .findJSessionId();

        if (jSessionId.isEmpty()) {
            httpHeaders.addValue("Set-Cookie", "JSESSIONID=" + UuidUtil.randomUuidString());
        }
    }

    public void addHeader(final String key, final String value) {
        headers.addValue(key, value);
    }

    public String generateResponse() {
        final String separator = "\r\n";
        return String.join(separator,
                generateStatusLine(),
                headers.generate(separator),
                body);
    }

    private String generateStatusLine() {
        return String.format("HTTP/1.1 %s %s ", statusCode.getCode(), statusCode.getMessage());
    }
}
