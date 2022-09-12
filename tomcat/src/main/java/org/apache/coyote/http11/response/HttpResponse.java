package org.apache.coyote.http11.response;

import java.io.File;
import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.utils.FileUtil;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final String body;

    public static HttpResponse of(final HttpStatusCode statusCode, final String url) {
        final File file = FileUtil.findFile(url);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(statusCode, contentType, responseBody);
    }

    public HttpResponse(final HttpStatusCode statusCode, final String contentType, final String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = initHeaders(contentType, body);
    }

    private HttpHeaders initHeaders(final String contentType, final String body) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addValue("Content-Type", contentType + ";charset=utf-8");
        httpHeaders.addValue("Content-Length", body.getBytes().length);
        return httpHeaders;
    }

    public void addJSessionId(final String jSessionId) {
        headers.addValue("Set-Cookie", "JSESSIONID=" + jSessionId);
    }

    public void addLocation(final String location) {
        headers.addValue("Location", location);
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
