package org.apache.coyote.http11.request;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader headers;
    private final QueryParameter queryParameter;
    private final Session session;
    private final String body;

    private HttpRequest(final HttpRequestLine requestLine, final HttpRequestHeader httpRequestHeader,
                        final QueryParameter queryParameter, final Session session, final String body) {
        this.requestLine = requestLine;
        this.headers = httpRequestHeader;
        this.queryParameter = queryParameter;
        this.session = session;
        this.body = body;
    }

    public static HttpRequest of(final HttpRequestLine requestLine, final HttpRequestHeader httpRequestHeader,
                                 final String requestBody) {
        final Session session = new Session(getJSessionId(httpRequestHeader));
        if (httpRequestHeader.isFormDataType()) {
            return new HttpRequest(requestLine, httpRequestHeader, QueryParameter.of(requestBody), session, "");
        }
        return new HttpRequest(requestLine, httpRequestHeader, QueryParameter.of(requestLine.getQueryString()), session,
                requestBody);
    }

    private static String getJSessionId(final HttpRequestHeader httpRequestHeader) {
        final HttpCookie cookies = httpRequestHeader.getCookies();
        if (cookies.contains("JSESSIONID")) {
            return cookies.getCookie("JSESSIONID");
        }
        return UUID.randomUUID().toString();
    }

    public boolean containsParameter(final String parameterName) {
        return queryParameter.contains(parameterName);
    }

    public String getParameter(final String parameterName) {
        return queryParameter.getParameter(parameterName);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(final String headerName) {
        return headers.getHeader(headerName);
    }

    public HttpCookie getCookies() {
        return headers.getCookies();
    }

    public Session getSession() {
        return session;
    }

    public String getBody() {
        return body;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }
}
