package org.apache.coyote.http11.request;

import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader headers;
    private final QueryParameter queryParameter;
    private final String body;

    private HttpRequest(final HttpRequestLine requestLine, final HttpRequestHeader httpRequestHeader,
                        final QueryParameter queryParameter, final String body) {
        this.requestLine = requestLine;
        this.headers = httpRequestHeader;
        this.queryParameter = queryParameter;
        this.body = body;
    }

    public static HttpRequest of(final HttpRequestLine requestLine, final HttpRequestHeader httpRequestHeader,
                                 final String requestBody) {
        if (httpRequestHeader.isFormDataType()) {
            return new HttpRequest(requestLine, httpRequestHeader, QueryParameter.of(requestBody), "");
        }
        return new HttpRequest(requestLine, httpRequestHeader, QueryParameter.of(requestLine.getQueryString()),
                requestBody);
    }

    public Session getSession() {
        final HttpCookie cookies = getCookies();
        if (cookies.contains("JSESSIONID")) {
            return new Session(cookies.getCookie("JSESSIONID"));
        }
        return new Session(UUID.randomUUID().toString());
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

    public String getBody() {
        return body;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }
}
