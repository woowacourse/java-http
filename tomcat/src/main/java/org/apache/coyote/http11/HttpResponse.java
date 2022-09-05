package org.apache.coyote.http11;

import java.io.File;
import java.util.Optional;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.utils.FileUtil;
import org.apache.coyote.http11.utils.UuidUtil;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String body;

    public static HttpResponse of(final HttpRequest httpRequest, final HttpStatus httpStatus, final String url) {
        final File file = FileUtil.findFile(url);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(httpRequest, httpStatus, contentType, responseBody);
    }

    public HttpResponse(final HttpRequest httpRequest, final HttpStatus httpStatus, final String contentType,
                        final String body) {
        this.httpStatus = httpStatus;
        this.body = body;
        this.httpHeaders = initHeaders(httpRequest, contentType, body);
    }

    private HttpHeaders initHeaders(final HttpRequest httpRequest, final String contentType, final String body) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addValue("Content-Type", contentType + ";charset=utf-8");
        httpHeaders.addValue("Content-Length", body.getBytes().length);

        generateJSession(httpRequest, httpHeaders);
        return httpHeaders;
    }

    private void generateJSession(final HttpRequest httpRequest, final HttpHeaders httpHeaders) {
        Optional<String> jSessionId = httpRequest.getHeaders()
                .findJSessionId();

        if (jSessionId.isEmpty()) {
            httpHeaders.addValue("Set-Cookie", "JSESSIONID=" + UuidUtil.randomUuidString());
        }
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
