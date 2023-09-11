package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpProtocol;
import org.apache.coyote.http11.common.HttpStatus;

import java.util.Map;
import java.util.StringJoiner;

public class HttpResponse {
    private static final String CRLF = "\r\n";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String EMPTY_LINE = "";
    private HttpStatusLine statusLine;
    private final HttpResponseHeaders headers;
    private HttpResponseBody body;

    private HttpResponse(final HttpStatusLine statusLine, final HttpResponseHeaders headers, final HttpResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(HttpStatusLine.empty(), HttpResponseHeaders.empty(), HttpResponseBody.empty());
    }

    public void ok(final String path) {
        this.statusLine = HttpStatusLine.of(HttpProtocol.HTTP_1_1, HttpStatus.OK);
        this.body = HttpResponseBody.from(path);

        headers.setContentType(body.getContentType());
        headers.setContentLength(body.getLength());
    }

    public void found(final String path) {
        this.statusLine = HttpStatusLine.of(HttpProtocol.HTTP_1_1, HttpStatus.FOUND);
        headers.setLocation(path);
    }

    public void home(final String content) {
        this.statusLine = HttpStatusLine.of(HttpProtocol.HTTP_1_1, HttpStatus.OK);
        this.body = body.home(content);

        headers.setContentType(body.getContentType());
        headers.setContentLength(body.getLength());
    }

    public void setCookie(final String jsessionid) {
        headers.setCookie(jsessionid);
    }

    public String message() {
        StringJoiner stringJoiner = new StringJoiner(CRLF);
        stringJoiner.add(statusLine.message());
        for (final Map.Entry<String, String> entry : headers.getHeaders().entrySet()) {
            final String headerFormat = String.format(HEADER_FORMAT, entry.getKey(), entry.getValue());
            stringJoiner.add(headerFormat);
        }
        stringJoiner.add(EMPTY_LINE);
        stringJoiner.add(body.getBody());
        return stringJoiner.toString();
    }
}
