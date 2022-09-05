package org.apache.coyote.http11.http.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.http.HttpVersion;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaderType;
import org.apache.coyote.http11.http.HttpHeaders;

public class HttpResponse {

    private static final String NEW_LINE_LETTER = "\r\n";
    private static final String EMPTY_LETTER = "";
    private static final String BLANK_LETTER = " ";
    private static final String COLON_LETTER = ":";
    private static final String SEMI_COLON_LETTER = ";";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpHeaders headers;
    private final String body;

    private HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus,
                        final HttpHeaders headers, final String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final HttpVersion httpVersion,
                                  final HttpStatus status,
                                  final String body,
                                  final HttpHeader... httpHeaders) {
        final HttpHeaders headers = HttpHeaders.of(httpHeaders);
        return new HttpResponse(httpVersion, status, headers, body);
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String generateResponse() {
        final String statusLine = generateStatusLine();
        final String headerLine = generateHeaderLine();
        return String.join(NEW_LINE_LETTER, statusLine, headerLine, EMPTY_LETTER, body);
    }

    private String generateStatusLine() {
        return String.join(BLANK_LETTER,
                httpVersion.getVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getMessage(),
                EMPTY_LETTER);
    }

    private String generateHeaderLine() {
        final List<String> headers = new ArrayList<>();
        for (HttpHeaderType httpHeaderType : this.headers.keySet()) {
            final String header = String.join(COLON_LETTER + BLANK_LETTER, httpHeaderType.getValue(),
                    String.join(SEMI_COLON_LETTER, this.headers.get(httpHeaderType).getValues()));
            headers.add(header + BLANK_LETTER);
        }
        return String.join(NEW_LINE_LETTER, headers);
    }
}
