package org.apache.coyote.http11.request;

import java.io.IOException;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeaders;
import org.apache.coyote.http11.request.startline.HttpMethod;
import org.apache.coyote.http11.request.startline.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody httpRequestBody;

    public static HttpRequest of(final HttpRequestParser parser) throws IOException {
        final RequestLine requestLine = parser.parseRequestLine();
        final RequestHeaders requestHeaders = parser.parseRequestHeaders();
        final RequestBody requestBody = parser.parseRequestBody(requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    public boolean matches(final HttpMethod httpMethod, final String requestPath) {
        return this.requestLine.isMethodEqualsTo(httpMethod) && this.requestLine.isPathEqualsTo(requestPath);
    }

    public String getBodyParameter(final String key) {
        return this.httpRequestBody.getParameter(key);
    }

    public String getStaticResourcePath() {
        return this.requestLine.getRequestPath();
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(requestHeaders);
    }

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final RequestBody httpRequestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.httpRequestBody = httpRequestBody;
    }
}
