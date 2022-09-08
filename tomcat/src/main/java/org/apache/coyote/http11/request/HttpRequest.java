package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.HttpCookie.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {

    private final RequestHeaders headers;
    private final RequestLine requestLine;
    private final QueryParams queryParams;

    public HttpRequest(final InputStream inputStream) throws IOException, URISyntaxException {
        final HttpMessage message = new HttpMessage(inputStream);
        this.requestLine = new RequestLine(message);
        this.headers = new RequestHeaders(message);
        this.queryParams = new QueryParams(getUri(), message);
    }

    public boolean isGetRequest() {
        return requestLine.isGet();
    }

    public boolean isRootPath() {
        return getPath().equals("/");
    }

    public String getPath() {
        return getUri().getPath();
    }

    private URI getUri() {
        try {
            return new URI("http://" + getHeaderValue("Host") + requestLine.getUri());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI requested");
        }
    }

    public boolean containsHeader(final String headerName) {
        return headers.contains(headerName);
    }

    public boolean hasSession() {
        return containsHeader(COOKIE) &&
            getHeaderValue(COOKIE).contains(JSESSIONID);
    }

    public Session getSession() {
        validateJSessionIdExist();
        final HttpCookie cookie = new HttpCookie(getHeaderValue(COOKIE));
        return new Session(cookie.getCookieValue(JSESSIONID));
    }

    private void validateJSessionIdExist() {
        if (!hasSession()) {
            throw new IllegalArgumentException("JSESSIONID Not Found");
        }
    }

    public String getHeaderValue(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public String getQueryValue(final String queryKey) {
        return queryParams.getQueryValue(queryKey);
    }

    public String getHttpVersion() {
        return requestLine.getVersion();
    }
}
