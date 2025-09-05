package org.apache.coyote.http11.reqeust.util;

import java.util.List;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.reqeust.HttpMethod;

public class HttpRequestParser {

    private static final HttpRequestParser instance = new HttpRequestParser();

    private HttpRequestParser() {
    }

    public HttpMethod parseHttpMethod(final String startLine) {
        final String method = startLine.split(" ")[0];

        return HttpMethod.valueOf(method);
    }

    public String parseRequestUri(final String startLine) {
        return startLine.split(" ")[1];
    }

    public HttpProtocolVersion parseHttpVersion(final String startLine) {
        final String httpVersion = startLine.split(" ")[2];

        return HttpProtocolVersion.valueOfVersion(httpVersion);
    }

    public HttpHeaders parseHeaders(final List<String> headerLines) {
        final HttpHeaders headers = new HttpHeaders();
        for (final String rawHeader : headerLines) {
            final String[] rawHeaders = rawHeader.split(": ");
            headers.addHeader(rawHeaders[0], rawHeaders[1]);
        }

        return headers;
    }

    public static HttpRequestParser getInstance() {
        return instance;
    }
}
