package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMessageBody;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.line.Uri;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpRequest(
            final RequestLine requestLine,
            final HttpHeaders httpHeaders,
            final HttpMessageBody httpMessageBody
    ) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public boolean methodEquals(Method method) {
        return requestLine.isMethod(method);
    }

    public boolean protocolEquals(HttpProtocol httpProtocol) {
        return requestLine.isHttpProtocol(httpProtocol);
    }

    public boolean uriEquals(Uri uri) {
        return requestLine.isUri(uri);
    }

    public boolean isUriHome() {
        return requestLine.isUriHome();
    }

    public boolean uriStartsWith(Uri uri) {
        return requestLine.isUriStartsWith(uri);
    }

    public String getUriPath() {
        return requestLine.getUriPath();
    }

    public String getQueryParameter(final String paramterName) {
        return requestLine.getQueryParameter(paramterName);
    }

    public String getFormData(final String name) {
        return httpMessageBody.getFormData(name);
    }

    public String getSessionId() {
        HttpCookies cookies = HttpCookies.from(httpHeaders);
        return cookies.getCookieValue("JSESSIONID");
    }
}
