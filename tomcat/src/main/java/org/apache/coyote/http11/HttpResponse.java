package org.apache.coyote.http11;

import java.io.File;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.utils.FileUtil;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final String contentType, final String body) {
        this.httpStatus = httpStatus;
        this.body = body;
        this.httpHeaders = initHeaders(contentType, body);
    }

    public static HttpResponse of(final HttpStatus httpStatus, final String url) {
        final File file = FileUtil.findFile(url);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(httpStatus, contentType, responseBody);
    }

    private HttpHeaders initHeaders(final String contentType, final String body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addValue("Content-Type", contentType + ";charset=utf-8");
        httpHeaders.addValue("Content-Length", body.getBytes().length);
        return httpHeaders;
    }

    public void addHeader(final String key, final String value) {
        httpHeaders.addValue(key, value);
    }

    public String generateResponse() {
        final String separator = "\r\n";
        return String.join(separator,
                generateStatusLine(),
                httpHeaders.generate(separator),
                body);
    }

    private String generateStatusLine() {
        return "HTTP/1.1 " + httpStatus.getValue() + " OK ";
    }
}
