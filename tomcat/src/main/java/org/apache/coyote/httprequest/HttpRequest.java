package org.apache.coyote.httprequest;

import org.apache.coyote.http11.common.CookieHeader;
import org.apache.coyote.httprequest.header.RequestHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpRequestLine httpRequestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final HttpRequestLine httpRequestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.httpRequestLine = httpRequestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final HttpRequestLine httpRequestLine = makeHttpRequestLine(bufferedReader.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
        final RequestBody requestBody = RequestBody.from(bufferedReader, requestHeaders.getContentLength());
        return new HttpRequest(httpRequestLine, requestHeaders, requestBody);
    }

    private static HttpRequestLine makeHttpRequestLine(final String line) {
        log.debug("Request line : " + line);
        return HttpRequestLine.from(line);
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public QueryString getQueryString() {
        return httpRequestLine.getQueryString();
    }

    public String getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }

    public RequestMethod getRequestMethod() {
        return httpRequestLine.getRequestMethod();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public CookieHeader getCookieHeader() {
        return requestHeaders.getCookieHeader();
    }
}
