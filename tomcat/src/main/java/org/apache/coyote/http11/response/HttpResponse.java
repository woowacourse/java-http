package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaderType;

public class HttpResponse {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private Map<HttpHeaderType, HttpHeader> headers;
    private String body;

    private HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus,
                         final Map<HttpHeaderType, HttpHeader> headers, final String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final HttpVersion httpVersion,
                                  final HttpStatus status,
                                  final String body,
                                  final HttpHeader... httpHeaders) {
        final Map<HttpHeaderType, HttpHeader> headers = new LinkedHashMap<>();
        for (HttpHeader httpHeader : httpHeaders) {
            headers.put(httpHeader.getHttpHeaderType(), httpHeader);
        }

        return new HttpResponse(httpVersion, status, headers, body);
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<HttpHeaderType, HttpHeader> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String generateResponse() {
        final String statusLine = generateStatusLine();
        final String headerLine = generateHeaderLine();
        return String.join("\r\n", statusLine, headerLine, "", body);
    }

    private String generateStatusLine() {
        return String.join(" ",
                httpVersion.getVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getMessage(),
                "");
    }

    private String generateHeaderLine() {
        final List<String> headers = new ArrayList<>();
        for (HttpHeaderType httpHeaderType : this.headers.keySet()) {
            final String header = String.join(": ", httpHeaderType.getValue(),
                    String.join(";", this.headers.get(httpHeaderType).getValues()));
            headers.add(header + " ");
        }
        return String.join("\r\n", headers);
    }
}
