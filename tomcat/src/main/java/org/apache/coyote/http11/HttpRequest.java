package org.apache.coyote.http11;

import static org.apache.coyote.http11.Http11Processor.SESSION_MANAGER;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.session.Session;

public class HttpRequest {

    private final RequestHeader requestHeader;
    private final QueryString queryString;
    private final RequestBody requestBody;
    private final Session session;

    private HttpRequest(final RequestHeader requestHeader, final RequestBody requestBody,
                        final QueryString queryString, final Session session) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.queryString = queryString;
        this.session = session;
    }

    public static HttpRequest of(final List<String> requestHeaderStrings, final String requestBodyString) {
        final RequestHeader requestHeader = RequestHeader.from(requestHeaderStrings);
        final RequestBody requestBody = RequestBody.from(requestBodyString);
        final QueryString queryString = QueryString.from(requestHeader.getOriginRequestURI());
        final Session session = findSessionId(requestHeader);
        return new HttpRequest(requestHeader, requestBody, queryString, session);
    }

    private static Session findSessionId(final RequestHeader requestHeader) {
        final String jsessionid = requestHeader.getCookie("JSESSIONID");
        if (Objects.isNull(jsessionid)) {
            return makeNewSession();
        }
        final Session findSession = SESSION_MANAGER.findSession(jsessionid);
        if (Objects.isNull(findSession)) {
            return makeNewSession();
        }

        if (findSession.isExpired()) {
            return makeNewSession();
        }

        return findSession;
    }

    private static Session makeNewSession() {
        final Session newSession = new Session();
        SESSION_MANAGER.add(newSession);
        return newSession;
    }

    public boolean isSameParsedRequestURI(final String uri) {
        return requestHeader.isSameParsedRequestURI(uri);
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.requestHeader.isSameHttpMethod(httpMethod);
    }

    public boolean hasCookie(final String cookie) {
        return requestHeader.hasCookie(cookie);
    }

    public String getCookieValue(final String key) {
        return requestHeader.getCookie(key);
    }

    public String getParsedRequestURI() {
        return requestHeader.getParsedRequestURI();
    }

    public String getOriginRequestURI() {
        return requestHeader.getOriginRequestURI();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpMethod getHttpMethod() {
        return requestHeader.getHttpMethod();
    }

    public HttpVersion getHttpVersion() {
        return requestHeader.getHttpVersion();
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public Session getSession() {
        return session;
    }
}
